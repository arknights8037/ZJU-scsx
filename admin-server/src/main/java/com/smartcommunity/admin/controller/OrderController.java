package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.OrderDetail;
import com.smartcommunity.admin.entity.Orders;
import com.smartcommunity.admin.mapper.OrderDetailMapper;
import com.smartcommunity.admin.mapper.OrdersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Orders> p = ordersMapper.selectPage(new Page<>(page, size), null);
        return Result.ok(p);
    }

    @GetMapping("/{orderNo}/details")
    public Result<List<OrderDetail>> details(@PathVariable String orderNo) {
        return Result.ok(orderDetailMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderNo, orderNo)));
    }
}
