package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.AdminOrderDetailView;
import com.smartcommunity.admin.dto.AdminOrderView;
import com.smartcommunity.admin.service.AdminOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class OrderController {
    private final AdminOrderService orderService;

    @GetMapping("/page")
    public Result<Page<AdminOrderView>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer orderState) {
        return Result.ok(orderService.page(page, size, keyword, orderState));
    }

    @GetMapping("/{orderNo}/details")
    public Result<List<AdminOrderDetailView>> details(@PathVariable String orderNo) {
        return Result.ok(orderService.details(orderNo));
    }
}
