package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.AdminOrderDashboard;
import com.smartcommunity.admin.dto.AdminOrderDetailView;
import com.smartcommunity.admin.dto.AdminOrderView;
import com.smartcommunity.admin.service.AdminOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单管理控制器，提供订单分页查询、仪表盘统计、订单明细查看以及退款审批功能。
 * 请求路径前缀：/api/admin/order
 */
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class OrderController {
    private final AdminOrderService orderService;

    /**
     * 分页查询订单列表，支持关键词和订单状态筛选。
     *
     * @param page       页码，默认 1
     * @param size       每页条数，默认 10
     * @param keyword    搜索关键词（订单号等）
     * @param orderState 订单状态筛选
     * @return 订单分页数据
     */
    @GetMapping("/page")
    public Result<Page<AdminOrderView>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer orderState) {
        return Result.ok(orderService.page(page, size, keyword, orderState));
    }

    /**
     * 查询订单仪表盘统计数据，可按关键词和状态过滤。
     *
     * @param keyword    搜索关键词
     * @param orderState 订单状态筛选
     * @return 订单仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<AdminOrderDashboard> dashboard(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer orderState) {
        return Result.ok(orderService.dashboard(keyword, orderState));
    }

    /**
     * 查询指定订单的明细列表（商品详情等）。
     *
     * @param orderNo 订单号
     * @return 订单明细视图列表
     */
    @GetMapping("/{orderNo}/details")
    public Result<List<AdminOrderDetailView>> details(@PathVariable String orderNo) {
        return Result.ok(orderService.details(orderNo));
    }

    /**
     * 审批通过指定订单的退款申请。
     *
     * @param orderNo 订单号
     * @return 操作结果
     */
    @PutMapping("/{orderNo}/refund/approve")
    public Result<Void> approveRefund(@PathVariable String orderNo) {
        orderService.approveRefund(orderNo);
        return Result.ok();
    }

    /**
     * 拒绝指定订单的退款申请。
     *
     * @param orderNo 订单号
     * @return 操作结果
     */
    @PutMapping("/{orderNo}/refund/reject")
    public Result<Void> rejectRefund(@PathVariable String orderNo) {
        orderService.rejectRefund(orderNo);
        return Result.ok();
    }

}
