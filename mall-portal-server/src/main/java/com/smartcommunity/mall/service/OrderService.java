package com.smartcommunity.mall.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.dto.LegacyOrderForm;
import com.smartcommunity.mall.dto.MonthSalesStat;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.entity.GoodsStore;
import com.smartcommunity.mall.entity.OrderDetail;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.mapper.CartsMapper;
import com.smartcommunity.mall.mapper.GoodsStoreMapper;
import com.smartcommunity.mall.mapper.OrderDetailMapper;
import com.smartcommunity.mall.mapper.OrdersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final CartsMapper cartsMapper;
    private final GoodsStoreMapper goodsStoreMapper;

    public Page<Orders> pageByUser(Integer userId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId).orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    public Page<Orders> pageByUser(Integer userId, Integer pageNo, Integer pageSize, String goodsName) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (goodsName != null && !goodsName.isBlank()) {
            wrapper.apply(
                "exists (select 1 from order_detail od inner join goods g on od.goods_no = g.goods_no " +
                    "where od.order_no = orders.order_no and g.goods_name like concat('%', {0}, '%'))",
                goodsName);
        }
        wrapper.orderByDesc("create_time");
        return ordersMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    public List<OrderDetail> getOrderDetails(String orderNo) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderNo, orderNo);
        return orderDetailMapper.selectList(wrapper);
    }

    @Transactional
    public Orders create(Integer userId, List<OrderDetail> items) {
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
            orderDetailMapper.insert(item);
        }

        return order;
    }

    public void updateState(String orderNo, Integer state) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = new Orders();
        order.setOrderState(state);
        ordersMapper.update(order, wrapper);
    }

    public String checkLegacyStock(LegacyOrderForm form) {
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
            double goodsPrice = goodsStore == null || goodsStore.getGoodsPrice() == null
                ? 0D
                : goodsStore.getGoodsPrice().doubleValue();

            OrderDetail detail = new OrderDetail();
            detail.setOrderNo(orderNo);
            detail.setStoreNo(item.getStoreNo());
            detail.setGoodsNo(item.getGoodsNo());
            detail.setGoodsAmount(item.getAmount());
            detail.setGoodsPrice(goodsPrice);
            detail.setTotalPrice(goodsPrice * item.getAmount());
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

    public List<MonthSalesStat> monthSales(Integer userId, Integer year) {
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

    private GoodsStore findGoodsStore(Carts item) {
        if (item.getGoodsStore() != null && item.getGoodsStore().getId() != null) {
            return item.getGoodsStore();
        }
        return goodsStoreMapper.selectOne(new LambdaQueryWrapper<GoodsStore>()
            .eq(GoodsStore::getStoreNo, item.getStoreNo())
            .eq(GoodsStore::getGoodsNo, item.getGoodsNo()));
    }
}
