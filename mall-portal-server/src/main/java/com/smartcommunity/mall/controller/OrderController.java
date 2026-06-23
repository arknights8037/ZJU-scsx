package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.PageResult;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.OrderDetail;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/page")
    public Result<PageResult<Orders>> page(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Orders> result = orderService.pageByUser(userId, page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/{orderNo}/details")
    public Result<List<OrderDetail>> details(@PathVariable String orderNo) {
        return Result.ok(orderService.getOrderDetails(orderNo));
    }

    @PostMapping("/create")
    public Result<Orders> create(@RequestParam Integer userId, @RequestBody List<OrderDetail> items) {
        return Result.ok(orderService.create(userId, items));
    }

    @PutMapping("/{orderNo}/state")
    public Result<Void> updateState(@PathVariable String orderNo, @RequestParam Integer state) {
        orderService.updateState(orderNo, state);
        return Result.ok();
    }
}
