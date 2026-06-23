package com.smartcommunity.mall.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.entity.OrderDetail;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.mapper.OrderDetailMapper;
import com.smartcommunity.mall.mapper.OrdersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单服务
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;

    public Page<Orders> pageByUser(Integer userId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId).orderByDesc(Orders::getCreateTime);
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
}
