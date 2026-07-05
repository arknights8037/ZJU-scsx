package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.dto.AdminOrderDashboard;
import com.smartcommunity.admin.dto.AdminOrderDetailView;
import com.smartcommunity.admin.dto.AdminOrderView;
import com.smartcommunity.admin.entity.AdminUser;
import com.smartcommunity.admin.entity.Goods;
import com.smartcommunity.admin.entity.OrderDetail;
import com.smartcommunity.admin.entity.Orders;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.mapper.AdminUserMapper;
import com.smartcommunity.admin.mapper.GoodsMapper;
import com.smartcommunity.admin.mapper.OrderDetailMapper;
import com.smartcommunity.admin.mapper.OrdersMapper;
import com.smartcommunity.admin.mapper.StoreMapper;
import com.smartcommunity.mall.entity.GoodsStore;
import com.smartcommunity.mall.mapper.GoodsStoreMapper;
import com.smartcommunity.mall.service.WalletService;
import com.smartcommunity.mall.service.PersonalExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理端订单管理服务。
 * 提供订单列表分页查询、仪表盘统计、订单详情、退款审核及自动取消/完成等能力。
 */
@Service
@RequiredArgsConstructor
public class AdminOrderService {

    // 订单主表：保存订单号、用户、金额、支付/配送/订单状态等汇总信息。
    private final OrdersMapper ordersMapper;
    // 订单明细表：保存每个订单中的商品、门店、数量和小计。
    private final OrderDetailMapper orderDetailMapper;
    // 用户、商品、门店 mapper 用来把编号翻译成后台页面可读的名称。
    private final AdminUserMapper userMapper;
    private final GoodsMapper goodsMapper;
    private final StoreMapper storeMapper;
    private final GoodsStoreMapper goodsStoreMapper;
    private final WalletService walletService;
    private final PersonalExpenseService personalExpenseService;

    /**
     * 分页查询订单列表，支持按关键词和订单状态筛选。
     * 查询前自动执行超时订单取消和已签收订单自动完成。
     *
     * @param page       页码
     * @param size       每页条数
     * @param keyword    关键词（匹配订单号、用户名、手机号）
     * @param orderState 订单状态筛选
     * @return 订单视图分页结果
     */
    public Page<AdminOrderView> page(int page, int size, String keyword, Integer orderState) {
        autoCancelUnpaidOrders();
        autoCompleteSignedOrders();
        // 对分页参数做保护，避免前端传入 0、负数或超大 size 导致查询异常。
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, Math.min(size, 100));
        LambdaQueryWrapper<Orders> wrapper = buildOrderWrapper(keyword, orderState);
        wrapper.orderByDesc(Orders::getCreateTime).orderByDesc(Orders::getId);

        Page<Orders> sourcePage = ordersMapper.selectPage(new Page<>(safePage, safeSize), wrapper);
        List<AdminOrderView> records = fillOrderViews(sourcePage.getRecords());
        Page<AdminOrderView> result = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 获取订单仪表盘统计数据。
     * 统计总订单数、总金额及各状态订单数量，筛选条件与列表保持一致。
     *
     * @param keyword    搜索关键词
     * @param orderState 订单状态筛选
     * @return 订单仪表盘数据
     */
    public AdminOrderDashboard dashboard(String keyword, Integer orderState) {
        autoCancelUnpaidOrders();
        autoCompleteSignedOrders();
        /*
         * 仪表盘统计和列表共用同一套筛选条件。
         * 这样后台搜索"某个手机号"时，顶部总金额/状态数量也会同步变成该搜索范围内的数据。
         */
        List<Orders> orders = ordersMapper.selectList(buildOrderWrapper(keyword, orderState));
        AdminOrderDashboard dashboard = new AdminOrderDashboard();
        dashboard.setTotalOrders((long) orders.size());
        dashboard.setTotalAmount(orders.stream()
            .map(Orders::getTotalPrice)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .sum());
        dashboard.setPendingPaymentCount(countByState(orders, 1));
        dashboard.setPaidCount(countByState(orders, 2));
        dashboard.setShippingCount(countByState(orders, 3));
        dashboard.setSignedCount(countByState(orders, 4));
        dashboard.setCompletedCount(countByState(orders, 9));
        dashboard.setRefundingCount(countByState(orders, 5));
        dashboard.setRefundedCount(countByState(orders, 6));
        dashboard.setClosedCount(countByState(orders, -1));
        dashboard.setCanceledCount(countByState(orders, -9));
        return dashboard;
    }

    /**
     * 获取订单详情，包含商品名称、图片、门店名称等填充信息。
     *
     * @param orderNo 订单号
     * @return 订单详情视图列表
     */
    public List<AdminOrderDetailView> details(String orderNo) {
        autoCancelUnpaidOrders();
        autoCompleteSignedOrders();
        // 详情页先拿订单明细，再批量补商品名、商品图、门店名，避免前端二次请求。
        List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
            .eq(OrderDetail::getOrderNo, orderNo)
            .orderByAsc(OrderDetail::getId));
        if (details.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Goods> goodsMap = goodsMap(details);
        Map<String, Store> storeMap = storeMap(details);
        return details.stream().map(detail -> {
            AdminOrderDetailView view = new AdminOrderDetailView();
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
     * 审核通过退款申请。
     * 余额支付的订单会将退款金额原路退回虚拟钱包，同时记录个人消费台账。
     *
     * @param orderNo 订单号
     */
    @Transactional
    public void approveRefund(String orderNo) {
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
            .eq(Orders::getOrderNo, orderNo)
            .last("limit 1 for update"));
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!Integer.valueOf(5).equals(order.getOrderState())) {
            throw new RuntimeException("只有退款中的订单可以确认退款");
        }
        if (Integer.valueOf(2).equals(order.getPaymentType())) {
            BigDecimal amount = order.getRefundAmount() == null
                    ? BigDecimal.valueOf(order.getTotalPrice() == null ? 0D : order.getTotalPrice())
                    : order.getRefundAmount();
            walletService.refundOrder(order.getUserId(), amount, order.getOrderNo());
        }
        BigDecimal refundAmount = order.getRefundAmount() == null
                ? BigDecimal.valueOf(order.getTotalPrice() == null ? 0D : order.getTotalPrice())
                : order.getRefundAmount();
        personalExpenseService.recordOrderRefund(order.getUserId(), order.getOrderNo(), refundAmount);
        order.setOrderState(6);
        order.setRefundHandledTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    /**
     * 先批量查关联数据，再在内存中组装。新人可以重点对比"循环里查数据库"的 N+1 写法。
     */
    private List<AdminOrderView> fillOrderViews(List<Orders> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> userIds = orders.stream().map(Orders::getUserId)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> orderNos = orders.stream().map(Orders::getOrderNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Integer, AdminUser> userMap = userIds.isEmpty() ? Collections.emptyMap()
            : userMapper.selectList(new LambdaQueryWrapper<AdminUser>().in(AdminUser::getId, userIds))
                .stream().collect(Collectors.toMap(AdminUser::getId, Function.identity(), (a, b) -> a));
        List<OrderDetail> allDetails = orderNos.isEmpty() ? Collections.emptyList()
            : orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
                .in(OrderDetail::getOrderNo, orderNos));
        Map<String, List<OrderDetail>> detailMap = allDetails.stream()
            .collect(Collectors.groupingBy(OrderDetail::getOrderNo));
        Map<String, Goods> goodsMap = goodsMap(allDetails);
        Map<String, Store> storeMap = storeMap(allDetails);

        return orders.stream().map(order -> {
            AdminOrderView view = new AdminOrderView();
            BeanUtils.copyProperties(order, view);
            AdminUser user = userMap.get(order.getUserId());
            view.setUserName(user == null ? "未知用户" : user.getUserName());
            view.setUserPhone(user == null ? "" : user.getPhone());

            List<OrderDetail> details = detailMap.getOrDefault(order.getOrderNo(), Collections.emptyList());
            view.setItemCount(details.stream().map(OrderDetail::getGoodsAmount)
                .filter(Objects::nonNull).mapToInt(Integer::intValue).sum());
            view.setGoodsSummary(summary(details.stream()
                .map(item -> {
                    Goods goods = goodsMap.get(item.getGoodsNo());
                    return firstText(item.getGoodsName(), goods == null ? item.getGoodsNo() : goods.getGoodsName());
                }).toList()));
            view.setStoreSummary(summary(details.stream()
                .map(item -> {
                    Store store = storeMap.get(item.getStoreNo());
                    return firstText(item.getStoreName(), store == null ? item.getStoreNo() : store.getStoreName());
                }).toList()));
            return view;
        }).toList();
    }

    /**
     * 构建订单查询条件包装器。
     * 关键词匹配订单号或用户姓名/手机号（用户匹配后转为 userId 查订单）。
     *
     * @param keyword    搜索关键词
     * @param orderState 订单状态
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<Orders> buildOrderWrapper(String keyword, Integer orderState) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orderState != null, Orders::getOrderState, orderState);

        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            // 关键词既可以匹配订单号，也可以匹配用户姓名/手机号；用户匹配后再转成 userId 查订单。
            List<Integer> userIds = userMapper.selectList(new LambdaQueryWrapper<AdminUser>()
                    .like(AdminUser::getUserName, value)
                    .or()
                    .like(AdminUser::getPhone, value))
                .stream().map(AdminUser::getId).toList();
            wrapper.and(condition -> {
                condition.like(Orders::getOrderNo, value);
                if (!userIds.isEmpty()) {
                    condition.or().in(Orders::getUserId, userIds);
                }
            });
        }
        return wrapper;
    }

    /**
     * 自动将超过 7 天未手动确认收货的已签收订单标记为已完成。
     */
    private void autoCompleteSignedOrders() {
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
     * 自动取消超过 24 小时未付款的订单，并恢复对应库存。
     */
    private void autoCancelUnpaidOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<Orders> unpaidOrders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
            .eq(Orders::getOrderState, 1)
            .le(Orders::getCreateTime, deadline));
        for (Orders order : unpaidOrders) {
            Orders update = new Orders();
            update.setOrderState(-9);
            int updated = ordersMapper.update(update, new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, order.getId())
                .eq(Orders::getOrderState, 1));
            if (updated > 0) {
                restoreOrderStock(order.getOrderNo());
            }
        }
    }

    /**
     * 订单取消后恢复各门店商品的库存数量。
     *
     * @param orderNo 被取消的订单号
     */
    private void restoreOrderStock(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            return;
        }
        List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
            .eq(OrderDetail::getOrderNo, orderNo));
        for (OrderDetail detail : details) {
            if (detail.getGoodsAmount() == null || detail.getGoodsAmount() <= 0) {
                continue;
            }
            GoodsStore goodsStore = goodsStoreMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GoodsStore>()
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
     * 统计订单列表中指定状态的订单数量。
     * 订单状态值约定：1-待付款, 2-已付款, 3-配送中, 4-已签收, 5-退款中, 6-已退款, 9-已完成等。
     *
     * @param orders 订单列表
     * @param state  目标状态值
     * @return 符合条件的订单数
     */
    private long countByState(List<Orders> orders, int state) {
        // 订单状态值来自旧项目约定：1 待付款、2 已付款、3 配送中、4 已签收、5 退款中、6 已退款、9 已完成等。
        return orders.stream()
            .map(Orders::getOrderState)
            .filter(Objects::nonNull)
            .filter(value -> value == state)
            .count();
    }

    /**
     * 从订单明细中收集商品编号，批量查询商品信息并返回 goodsNo -> Goods 映射。
     *
     * @param details 订单明细列表
     * @return 商品编号到商品对象的映射
     */
    private Map<String, Goods> goodsMap(List<OrderDetail> details) {
        // 先收集商品编号，再 in 查询成 Map，给列表/详情组装人类可读的商品名称。
        Set<String> goodsNos = details.stream().map(OrderDetail::getGoodsNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        if (goodsNos.isEmpty()) {
            return Collections.emptyMap();
        }
        return goodsMapper.selectList(new LambdaQueryWrapper<Goods>().in(Goods::getGoodsNo, goodsNos))
            .stream().collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
    }

    /**
     * 从订单明细中收集门店编号，批量查询门店信息并返回 storeNo -> Store 映射。
     *
     * @param details 订单明细列表
     * @return 门店编号到门店对象的映射
     */
    private Map<String, Store> storeMap(List<OrderDetail> details) {
        // 和商品一样，门店也按 storeNo 批量查询，避免 N+1 查询。
        Set<String> storeNos = details.stream().map(OrderDetail::getStoreNo)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        if (storeNos.isEmpty()) {
            return Collections.emptyMap();
        }
        return storeMapper.selectList(new LambdaQueryWrapper<Store>().in(Store::getStoreNo, storeNos))
            .stream().collect(Collectors.toMap(Store::getStoreNo, Function.identity(), (a, b) -> a));
    }

    private String summary(List<String> names) {
        // 后台列表空间有限，只展示前两个不同名称；超过两个时用"等 N 种"概括。
        LinkedHashSet<String> distinctNames = names.stream()
            .filter(StringUtils::hasText).collect(Collectors.toCollection(LinkedHashSet::new));
        if (distinctNames.isEmpty()) {
            return "暂无信息";
        }
        List<String> firstNames = distinctNames.stream().limit(2).toList();
        String result = String.join("、", firstNames);
        return distinctNames.size() > 2 ? result + " 等" + distinctNames.size() + "种" : result;
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
}
