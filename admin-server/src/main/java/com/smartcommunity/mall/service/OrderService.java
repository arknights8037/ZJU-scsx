package com.smartcommunity.mall.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.dto.LegacyOrderForm;
import com.smartcommunity.mall.dto.MonthSalesStat;
import com.smartcommunity.mall.dto.OrderDetailView;
import com.smartcommunity.mall.dto.RefundRequest;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.entity.Goods;
import com.smartcommunity.mall.entity.GoodsStore;
import com.smartcommunity.mall.entity.OrderDetail;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.mapper.CartsMapper;
import com.smartcommunity.mall.mapper.GoodsMapper;
import com.smartcommunity.mall.mapper.GoodsStoreMapper;
import com.smartcommunity.mall.mapper.OrderDetailMapper;
import com.smartcommunity.mall.mapper.OrdersMapper;
import com.smartcommunity.mall.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 居民端订单服务。
 *
 * 这里同时保留两套能力：
 * 1. 新居民端：从购物车 id 创建订单，后端重新计算价格和库存；
 * 2. 老师原始示例：Legacy 方法兼容旧接口，减少迁移时对旧页面的破坏。
 *
 * 订单状态约定：1-待付款, 2-已付款, 3-配送中, 4-已签收, 5-退款中, 6-已退款, 9-已完成, -9-已取消。
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final CartsMapper cartsMapper;
    private final GoodsStoreMapper goodsStoreMapper;
    private final GoodsMapper goodsMapper;
    private final StoreMapper storeMapper;
    private final SpecialService specialService;
    private final WalletService walletService;
    private final PersonalExpenseService personalExpenseService;

    public Page<Orders> pageByUser(Integer userId, Integer pageNo, Integer pageSize) {
        autoCancelUnpaidOrders();
        autoCompleteSignedOrders();
        // 居民只能查看自己的订单，userId 由 JWT 过滤器注入，不能由前端自由传入。
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId).orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    /**
     * 分页查询当前用户的订单（支持按商品名搜索）。
     * 使用 exists 子查询避免先查 orderNo 再回表。
     *
     * @param userId    用户 ID
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param goodsName 商品名称关键词（可选）
     * @return 订单分页结果
     */
    public Page<Orders> pageByUser(Integer userId, Integer pageNo, Integer pageSize, String goodsName) {
        autoCancelUnpaidOrders();
        autoCompleteSignedOrders();
        // 兼容旧接口的"按商品名搜索订单"：用 exists 子查询避免先查一堆 orderNo 再回表。
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (goodsName != null && !goodsName.isBlank()) {
            wrapper.apply(
                "exists (select 1 from order_detail od left join goods g on od.goods_no = g.goods_no " +
                    "where od.order_no = orders.order_no " +
                    "and (od.goods_name like concat('%', {0}, '%') or g.goods_name like concat('%', {0}, '%')))",
                goodsName);
        }
        wrapper.orderByDesc("create_time");
        return ordersMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    /**
     * 根据订单号获取订单明细。
     *
     * @param orderNo 订单号
     * @return 订单明细列表
     */
    public List<OrderDetail> getOrderDetails(String orderNo) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderNo, orderNo);
        return orderDetailMapper.selectList(wrapper);
    }

    /**
     * 获取订单详情（含商品名、图片、门店名称等），同时校验当前用户是否是订单所属人。
     *
     * @param userId  用户 ID
     * @param orderNo 订单号
     * @return 订单详情视图列表
     */
    public List<OrderDetailView> getOrderDetails(Integer userId, String orderNo) {
        // 先校验订单是否属于当前用户，防止用户猜订单号查看别人订单。
        getUserOrder(userId, orderNo);
        return fillDetailViews(getOrderDetails(orderNo));
    }

    @Transactional
    public Orders create(Integer userId, List<OrderDetail> items) {
        // 旧接口直接传明细进来；新接口优先使用 createFromCart，因为安全性更好。
        String orderNo = IdUtil.fastSimpleUUID().toUpperCase();
        double totalPrice = items.stream().mapToDouble(OrderDetail::getTotalPrice).sum();

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setOrderState(1); // 未付款
        ordersMapper.insert(order);

        for (OrderDetail item : items) {
            item.setOrderNo(orderNo);
            fillOrderSnapshot(item);
            orderDetailMapper.insert(item);
        }

        return order;
    }

    /**
     * 居民端从购物车结算：前端只提交选中的购物车 id。
     * 价格、库存和用户归属全部由后端重新查询，避免相信浏览器里的金额和 userId。
     */
    @Transactional
    public Orders createFromCart(Integer userId, List<Integer> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            throw new RuntimeException("请选择要结算的商品");
        }

        List<Carts> carts = cartsMapper.selectList(new LambdaQueryWrapper<Carts>()
            .eq(Carts::getUserId, userId)
            .in(Carts::getId, cartIds));
        if (carts.size() != cartIds.size()) {
            throw new RuntimeException("购物车商品不存在或不属于当前用户");
        }

        String orderNo = IdUtil.fastSimpleUUID().toUpperCase();
        double totalPrice = 0D;
        List<OrderDetail> details = new ArrayList<>();
        // 记录每个 goods_store 应扣减多少库存，订单明细插入成功后再统一扣减。
        Map<GoodsStore, Integer> stockChanges = new LinkedHashMap<>();

        for (Carts cart : carts) {
            // 数量异常时兜底成 1，避免出现 0 件或负数订单。
            int amount = cart.getAmount() == null || cart.getAmount() < 1 ? 1 : cart.getAmount();
            GoodsStore goodsStore = findGoodsStore(cart);
            if (goodsStore == null) {
                throw new RuntimeException("商品门店信息不存在");
            }
            if (goodsStore.getGoodsStock() != null && goodsStore.getGoodsStock() < amount) {
                throw new RuntimeException("商品库存不足");
            }
            Goods goods = requireGoods(cart.getGoodsNo());
            Store store = requireStore(cart.getStoreNo());
            BigDecimal effectivePrice = specialService.effectivePrice(cart.getGoodsNo(), goodsStore.getGoodsPrice());
            double goodsPrice = effectivePrice == null ? 0D : effectivePrice.doubleValue();
            double lineTotal = goodsPrice * amount;
            totalPrice += lineTotal;

            // 订单明细只保存交易快照需要的字段：商品编号、门店编号、购买数量、当时价格和小计。
            OrderDetail detail = new OrderDetail();
            detail.setOrderNo(orderNo);
            detail.setStoreNo(cart.getStoreNo());
            detail.setGoodsNo(cart.getGoodsNo());
            detail.setGoodsAmount(amount);
            detail.setGoodsPrice(goodsPrice);
            detail.setTotalPrice(lineTotal);
            applyOrderSnapshot(detail, goods, store);
            details.add(detail);
            stockChanges.put(goodsStore, amount);
        }

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        // 这里是模拟商城：默认在线支付、社区配送、创建后待付款。
        order.setPaymentType(1);
        order.setPaymentSubtype(1);
        order.setDeliveryType(1);
        order.setOrderState(1);
        ordersMapper.insert(order);

        for (OrderDetail detail : details) {
            orderDetailMapper.insert(detail);
        }
        for (Map.Entry<GoodsStore, Integer> entry : stockChanges.entrySet()) {
            GoodsStore goodsStore = entry.getKey();
            if (goodsStore.getGoodsStock() != null) {
                // 课程项目没有引入复杂库存锁；这里在事务内扣减，适合单机演示。
                goodsStore.setGoodsStock(goodsStore.getGoodsStock() - entry.getValue());
                goodsStoreMapper.updateById(goodsStore);
            }
        }
        for (Carts cart : carts) {
            // 订单创建成功后移除已结算购物车项，避免重复下单。
            cartsMapper.deleteById(cart.getId());
        }
        return order;
    }

    /**
     * 立即购买：根据前端传入的单品信息创建订单。
     *
     * @param userId 用户 ID
     * @param item   购买的临时购物车项（含商品、门店、数量）
     * @return 创建成功的订单
     */
    @Transactional
    public Orders createBuyNow(Integer userId, Carts item) {
        if (item == null || item.getGoodsNo() == null || item.getStoreNo() == null) {
            throw new RuntimeException("请选择商品和门店");
        }
        int amount = item.getAmount() == null || item.getAmount() < 1 ? 1 : item.getAmount();
        GoodsStore goodsStore = findGoodsStore(item);
        if (goodsStore == null) {
            throw new RuntimeException("商品门店信息不存在");
        }
        if (goodsStore.getGoodsStock() != null && goodsStore.getGoodsStock() < amount) {
            throw new RuntimeException("商品库存不足");
        }
        Goods goods = requireGoods(item.getGoodsNo());
        Store store = requireStore(item.getStoreNo());

        String orderNo = IdUtil.fastSimpleUUID().toUpperCase();
        BigDecimal effectivePrice = specialService.effectivePrice(item.getGoodsNo(), goodsStore.getGoodsPrice());
        double goodsPrice = effectivePrice == null ? 0D : effectivePrice.doubleValue();
        double totalPrice = goodsPrice * amount;

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setPaymentType(1);
        order.setPaymentSubtype(1);
        order.setDeliveryType(1);
        order.setOrderState(1);
        ordersMapper.insert(order);

        OrderDetail detail = new OrderDetail();
        detail.setOrderNo(orderNo);
        detail.setStoreNo(item.getStoreNo());
        detail.setGoodsNo(item.getGoodsNo());
        detail.setGoodsAmount(amount);
        detail.setGoodsPrice(goodsPrice);
        detail.setTotalPrice(totalPrice);
        applyOrderSnapshot(detail, goods, store);
        orderDetailMapper.insert(detail);

        if (goodsStore.getGoodsStock() != null) {
            goodsStore.setGoodsStock(goodsStore.getGoodsStock() - amount);
            goodsStoreMapper.updateById(goodsStore);
        }
        return order;
    }

    /**
     * 直接更新订单状态（用于管理端或其他内部调用）。
     *
     * @param orderNo 订单号
     * @param state   新的状态值
     */
    public void updateState(String orderNo, Integer state) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = new Orders();
        order.setOrderState(state);
        ordersMapper.update(order, wrapper);
    }

    /**
     * 支付订单。支持钱包支付和模拟在线支付两种方式。
     * 钱包支付会扣除虚拟余额；在线支付仅修改订单状态。
     *
     * @param userId         用户 ID
     * @param orderNo        订单号
     * @param paymentMethod  支付方式（"WALLET" 表示余额支付）
     */
    @Transactional
    public void pay(Integer userId, String orderNo, String paymentMethod) {
        autoCancelUnpaidOrders();
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .eq(Orders::getOrderNo, orderNo)
                .last("limit 1 for update"));
        if (order == null) throw new RuntimeException("订单不存在");
        if (!Integer.valueOf(1).equals(order.getOrderState())) {
            throw new RuntimeException("当前订单状态不可支付");
        }
        boolean walletPayment = "WALLET".equalsIgnoreCase(paymentMethod);
        if (walletPayment) {
            walletService.payForOrder(userId, BigDecimal.valueOf(order.getTotalPrice()), orderNo);
            order.setPaymentType(2);
            order.setPaymentSubtype(2);
        } else {
            order.setPaymentType(1);
            order.setPaymentSubtype(1);
        }
        order.setOrderState(2);
        ordersMapper.updateById(order);
        personalExpenseService.recordOrderExpense(userId, orderNo, BigDecimal.valueOf(order.getTotalPrice()));
    }

    /**
     * 用户取消未付款订单。
     *
     * @param userId  用户 ID
     * @param orderNo 订单号
     */
    @Transactional
    public void cancel(Integer userId, String orderNo) {
        autoCancelUnpaidOrders();
        Orders order = getUserOrder(userId, orderNo);
        if (!Integer.valueOf(1).equals(order.getOrderState())) {
            throw new RuntimeException("只有未付款订单可以取消");
        }
        cancelUnpaidOrder(order);
    }

    public void sign(Integer userId, String orderNo) {
        // 居民确认签收后，订单从已付款/配送中流转为已签收；updateTime 记录签收时间。
        Orders order = getUserOrder(userId, orderNo);
        if (!Integer.valueOf(2).equals(order.getOrderState()) && !Integer.valueOf(3).equals(order.getOrderState())) {
            throw new RuntimeException("当前订单状态不可确认签收");
        }
        order.setOrderState(4);
        ordersMapper.updateById(order);
    }

    public void confirmReceipt(Integer userId, String orderNo) {
        // 居民手动确认收货后，订单完成。未手动确认的已签收订单会在 7 天后自动完成。
        autoCompleteSignedOrders();
        Orders order = getUserOrder(userId, orderNo);
        if (!Integer.valueOf(4).equals(order.getOrderState())) {
            throw new RuntimeException("当前订单状态不可确认收货");
        }
        order.setOrderState(9);
        ordersMapper.updateById(order);
    }

    /**
     * 用户申请退款。已付款/配送中/已签收/已完成的订单可申请退款。
     *
     * @param userId  用户 ID
     * @param orderNo 订单号
     * @param request 退款请求（含金额和原因）
     */
    public void requestRefund(Integer userId, String orderNo, RefundRequest request) {
        Orders order = getUserOrder(userId, orderNo);
        Integer state = order.getOrderState();
        if (!Integer.valueOf(2).equals(state)
            && !Integer.valueOf(3).equals(state)
            && !Integer.valueOf(4).equals(state)
            && !Integer.valueOf(9).equals(state)) {
            throw new RuntimeException("当前订单状态不可申请退款");
        }
        BigDecimal total = BigDecimal.valueOf(order.getTotalPrice() == null ? 0D : order.getTotalPrice());
        BigDecimal refundAmount = request == null || request.getRefundAmount() == null
            ? total
            : request.getRefundAmount();
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0 || refundAmount.compareTo(total) > 0) {
            throw new RuntimeException("退款金额必须大于 0 且不能超过订单实付金额");
        }
        String reason = request == null ? null : request.getRefundReason();
        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("请填写退款原因");
        }
        order.setOrderState(5);
        order.setRefundAmount(refundAmount);
        order.setRefundReason(reason.trim());
        order.setRefundDescription(request.getRefundDescription());
        order.setRefundTime(LocalDateTime.now());
        order.setRefundHandledTime(null);
        ordersMapper.updateById(order);
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000L, initialDelay = 60 * 1000L)
    public void autoCompleteSignedOrders() {
        autoCancelUnpaidOrders();
        // 使用 update_time 作为签收时间：状态更新为 4 时，MyBatis 自动写入 updateTime。
        LocalDateTime deadline = LocalDateTime.now().minusDays(7);
        List<Orders> signedOrders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
            .eq(Orders::getOrderState, 4)
            .and(wrapper -> wrapper
                .le(Orders::getUpdateTime, deadline)
                .or()
                .isNull(Orders::getUpdateTime)
                .le(Orders::getCreateTime, deadline)));
        for (Orders order : signedOrders) {
            order.setOrderState(9);
            ordersMapper.updateById(order);
        }
    }

    /**
     * 自动取消超过 24 小时未付款的订单，并恢复商品库存。
     */
    @Transactional
    public void autoCancelUnpaidOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<Orders> unpaidOrders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
            .eq(Orders::getOrderState, 1)
            .le(Orders::getCreateTime, deadline));
        for (Orders order : unpaidOrders) {
            cancelUnpaidOrder(order);
        }
    }

    public String checkLegacyStock(LegacyOrderForm form) {
        // 旧接口下单前的库存预检查，保留给老师原始页面调用。
        if (form.getRecords() == null) {
            return null;
        }
        for (Carts item : form.getRecords()) {
            GoodsStore goodsStore = findGoodsStore(item);
            if (goodsStore != null && goodsStore.getGoodsStock() != null
                && goodsStore.getGoodsStock() - item.getAmount() < 0) {
                return item.getGoods() == null ? item.getGoodsNo() : item.getGoods().getGoodsName();
            }
        }
        return null;
    }

    /**
     * 兼容旧接口创建订单。保留给老师原始页面调用。
     *
     * @param form 旧版订单表单
     * @return 订单号
     */
    @Transactional
    public String createLegacy(LegacyOrderForm form) {
        String orderNo = IdUtil.fastSimpleUUID();
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(form.getUserId());
        order.setTotalPrice(form.getTotalPrice());
        order.setPaymentType(1);
        order.setPaymentSubtype(1);
        order.setDeliveryType(1);
        order.setOrderState(1);
        ordersMapper.insert(order);

        if (form.getRecords() == null) {
            return orderNo;
        }
        for (Carts item : form.getRecords()) {
            GoodsStore goodsStore = findGoodsStore(item);
            BigDecimal effectivePrice = goodsStore == null
                ? null : specialService.effectivePrice(item.getGoodsNo(), goodsStore.getGoodsPrice());
            double goodsPrice = effectivePrice == null ? 0D : effectivePrice.doubleValue();

            OrderDetail detail = new OrderDetail();
            detail.setOrderNo(orderNo);
            detail.setStoreNo(item.getStoreNo());
            detail.setGoodsNo(item.getGoodsNo());
            detail.setGoodsAmount(item.getAmount());
            detail.setGoodsPrice(goodsPrice);
            detail.setTotalPrice(goodsPrice * item.getAmount());
            fillOrderSnapshot(detail);
            orderDetailMapper.insert(detail);

            if (item.getId() != null) {
                cartsMapper.deleteById(item.getId());
            }
            if (goodsStore != null && goodsStore.getGoodsStock() != null) {
                goodsStore.setGoodsStock(goodsStore.getGoodsStock() - item.getAmount());
                goodsStoreMapper.updateById(goodsStore);
            }
        }
        return orderNo;
    }

    /**
     * 获取指定年份各月的消费统计，用于老商城统计图。
     *
     * @param userId 用户 ID
     * @param year   年份
     * @return 月份销售统计数据列表
     */
    public List<MonthSalesStat> monthSales(Integer userId, Integer year) {
        // 按月份聚合当前用户消费额，用于老商城统计图。
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.select("month(create_time) as month", "sum(total_price) as price")
            .eq("user_id", userId)
            .apply("year(create_time) = {0}", year)
            .groupBy("month(create_time)")
            .orderByAsc("month(create_time)");

        List<Map<String, Object>> rows = ordersMapper.selectMaps(wrapper);
        Map<Integer, Double> monthPrice = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Number month = (Number) row.get("month");
            Number price = (Number) row.get("price");
            if (month != null) {
                monthPrice.put(month.intValue(), price == null ? 0D : price.doubleValue());
            }
        }

        List<MonthSalesStat> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            result.add(new MonthSalesStat(month, monthPrice.getOrDefault(month, 0D)));
        }
        return result;
    }

    /**
     * 根据购物车项查找对应的门店-商品关联关系。
     * 优先复用购物车中已加载的 goodsStore 对象。
     *
     * @param item 购物车项
     * @return 门店商品关联关系
     */
    private GoodsStore findGoodsStore(Carts item) {
        // 如果购物车对象已经带了 goodsStore 关联对象，优先复用；否则按 storeNo + goodsNo 查询。
        if (item.getGoodsStore() != null && item.getGoodsStore().getId() != null) {
            return item.getGoodsStore();
        }
        return goodsStoreMapper.selectOne(new LambdaQueryWrapper<GoodsStore>()
            .eq(GoodsStore::getStoreNo, item.getStoreNo())
            .eq(GoodsStore::getGoodsNo, item.getGoodsNo()));
    }

    /**
     * 将订单明细批量填充商品和门店信息后转换为详情视图。
     *
     * @param details 订单明细列表
     * @return 订单详情视图列表
     */
    private List<OrderDetailView> fillDetailViews(List<OrderDetail> details) {
        if (details.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> goodsNos = details.stream().map(OrderDetail::getGoodsNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> storeNos = details.stream().map(OrderDetail::getStoreNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, Goods> goodsMap = goodsNos.isEmpty() ? Collections.emptyMap()
            : goodsMapper.selectList(new LambdaQueryWrapper<Goods>().in(Goods::getGoodsNo, goodsNos))
                .stream().collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
        Map<String, Store> storeMap = storeNos.isEmpty() ? Collections.emptyMap()
            : storeMapper.selectList(new LambdaQueryWrapper<Store>().in(Store::getStoreNo, storeNos))
                .stream().collect(Collectors.toMap(Store::getStoreNo, Function.identity(), (a, b) -> a));

        return details.stream().map(detail -> {
            OrderDetailView view = new OrderDetailView();
            BeanUtils.copyProperties(detail, view);
            Goods goods = goodsMap.get(detail.getGoodsNo());
            Store store = storeMap.get(detail.getStoreNo());
            view.setGoodsName(firstText(detail.getGoodsName(), goods == null ? detail.getGoodsNo() : goods.getGoodsName()));
            view.setGoodsPicture(firstText(detail.getGoodsPicture(), goods == null ? null : goods.getGoodsPicture()));
            view.setStoreName(firstText(detail.getStoreName(), store == null ? detail.getStoreNo() : store.getStoreName()));
            return view;
        }).toList();
    }

    /**
     * 取消单条未付款订单，乐观锁避免并发重复取消。
     * 取消成功后恢复对应库存。
     *
     * @param order 待取消的订单
     */
    private void cancelUnpaidOrder(Orders order) {
        Orders update = new Orders();
        update.setOrderState(-9);
        int updated = ordersMapper.update(update, new LambdaQueryWrapper<Orders>()
            .eq(Orders::getId, order.getId())
            .eq(Orders::getOrderState, 1));
        if (updated > 0) {
            order.setOrderState(-9);
            restoreOrderStock(order.getOrderNo());
        }
    }

    /**
     * 订单取消后恢复各门店商品的库存。
     *
     * @param orderNo 被取消的订单号
     */
    private void restoreOrderStock(String orderNo) {
        if (orderNo == null || orderNo.isBlank()) {
            return;
        }
        List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
            .eq(OrderDetail::getOrderNo, orderNo));
        for (OrderDetail detail : details) {
            if (detail.getGoodsAmount() == null || detail.getGoodsAmount() <= 0) {
                continue;
            }
            GoodsStore goodsStore = goodsStoreMapper.selectOne(new LambdaQueryWrapper<GoodsStore>()
                .eq(GoodsStore::getStoreNo, detail.getStoreNo())
                .eq(GoodsStore::getGoodsNo, detail.getGoodsNo())
                .last("limit 1"));
            if (goodsStore == null || goodsStore.getGoodsStock() == null) {
                continue;
            }
            goodsStore.setGoodsStock(goodsStore.getGoodsStock() + detail.getGoodsAmount());
            goodsStoreMapper.updateById(goodsStore);
        }
    }

    /**
     * 填充订单明细的交易快照（商品名、图片、门店名）。
     * 即使后续商品下架或改名，历史订单仍保留当时的快照信息。
     *
     * @param detail 订单明细
     */
    private void fillOrderSnapshot(OrderDetail detail) {
        // 旧接口可能只传商品编号和门店编号；保存前仍尽量补齐交易快照。
        Goods goods = findGoods(detail.getGoodsNo());
        Store store = findStore(detail.getStoreNo());
        applyOrderSnapshot(detail, goods, store);
    }

    /**
     * 将商品和门店信息写入订单明细作为交易快照。
     *
     * @param detail 订单明细
     * @param goods  商品对象
     * @param store  门店对象
     */
    private void applyOrderSnapshot(OrderDetail detail, Goods goods, Store store) {
        // 交易快照写入 order_detail，后续商品改名/下架/删除也不会影响历史订单展示。
        detail.setGoodsName(firstText(detail.getGoodsName(), goods == null ? detail.getGoodsNo() : goods.getGoodsName()));
        detail.setGoodsPicture(firstText(detail.getGoodsPicture(), goods == null ? null : goods.getGoodsPicture()));
        detail.setStoreName(firstText(detail.getStoreName(), store == null ? detail.getStoreNo() : store.getStoreName()));
    }

    /**
     * 根据商品编号获取商品，不存在则抛异常。
     *
     * @param goodsNo 商品编号
     * @return Goods 对象
     */
    private Goods requireGoods(String goodsNo) {
        Goods goods = findGoods(goodsNo);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        return goods;
    }

    /**
     * 根据门店编号获取门店，不存在则抛异常。
     *
     * @param storeNo 门店编号
     * @return Store 对象
     */
    private Store requireStore(String storeNo) {
        Store store = findStore(storeNo);
        if (store == null) {
            throw new RuntimeException("门店不存在");
        }
        return store;
    }

    /**
     * 根据商品编号查询商品。
     *
     * @param goodsNo 商品编号
     * @return Goods 对象（可能为 null）
     */
    private Goods findGoods(String goodsNo) {
        if (goodsNo == null || goodsNo.isBlank()) {
            return null;
        }
        return goodsMapper.selectOne(new LambdaQueryWrapper<Goods>()
            .eq(Goods::getGoodsNo, goodsNo)
            .last("limit 1"));
    }

    /**
     * 根据门店编号查询门店。
     *
     * @param storeNo 门店编号
     * @return Store 对象（可能为 null）
     */
    private Store findStore(String storeNo) {
        if (storeNo == null || storeNo.isBlank()) {
            return null;
        }
        return storeMapper.selectOne(new LambdaQueryWrapper<Store>()
            .eq(Store::getStoreNo, storeNo)
            .last("limit 1"));
    }

    /**
     * 取第一个非空字符串，若 value 为空则返回 fallback。
     *
     * @param value    首选值
     * @param fallback 备选值
     * @return 非空字符串
     */
    private String firstText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    /**
     * 根据用户 ID 和订单号获取订单，同时校验订单归属。
     *
     * @param userId  用户 ID
     * @param orderNo 订单号
     * @return Orders 对象
     */
    private Orders getUserOrder(Integer userId, String orderNo) {
        // 所有订单详情/付款操作都先走这个方法做归属校验。
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
            .eq(Orders::getUserId, userId)
            .eq(Orders::getOrderNo, orderNo));
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }
}
