package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.PageResult;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.RefundRequest;
import com.smartcommunity.mall.dto.OrderDetailView;
import com.smartcommunity.mall.dto.OrderPayRequest;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.entity.OrderDetail;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器，提供订单分页查询、详情、创建、购买、支付、取消、签收、确认收货和退款功能。
 * 请求路径前缀：/api/order
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 分页查询当前用户的订单列表。
     *
     * @param userId 当前登录用户ID
     * @param page   页码，默认第1页
     * @param size   每页条数，默认10条
     * @return 订单分页结果的通用响应
     */
    @GetMapping("/page")
    public Result<PageResult<Orders>> page(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Orders> result = orderService.pageByUser(userId, page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 获取指定订单的详情（商品明细列表）。
     *
     * @param userId  当前登录用户ID
     * @param orderNo 订单编号
     * @return 订单详情视图列表的通用响应
     */
    @GetMapping("/{orderNo}/details")
    public Result<List<OrderDetailView>> details(@RequestAttribute Integer userId, @PathVariable String orderNo) {
        return Result.ok(orderService.getOrderDetails(userId, orderNo));
    }

    /**
     * 从购物车中选择商品创建订单。
     *
     * @param userId  当前登录用户ID
     * @param cartIds 选中的购物车记录ID列表
     * @return 创建成功的订单的通用响应
     */
    @PostMapping("/create")
    public Result<Orders> create(@RequestAttribute Integer userId, @RequestBody List<Integer> cartIds) {
        return Result.ok(orderService.createFromCart(userId, cartIds));
    }

    /**
     * 立即购买接口（跳过购物车，直接创建订单）。
     *
     * @param userId 当前登录用户ID
     * @param item   要购买的商品信息（商品编号、数量等）
     * @return 创建成功的订单的通用响应
     */
    @PostMapping("/buy-now")
    public Result<Orders> buyNow(@RequestAttribute Integer userId, @RequestBody Carts item) {
        return Result.ok(orderService.createBuyNow(userId, item));
    }

    /**
     * 支付指定订单。
     *
     * @param userId  当前登录用户ID
     * @param orderNo 订单编号
     * @param request 支付请求（支付方式，默认QR扫码支付）
     * @return 空成功的通用响应
     */
    @PutMapping("/{orderNo}/pay")
    public Result<Void> pay(@RequestAttribute Integer userId,
            @PathVariable String orderNo,
            @RequestBody(required = false) OrderPayRequest request) {
        // 未指定支付方式时默认使用扫码支付
        orderService.pay(userId, orderNo, request == null ? "QR" : request.getPaymentMethod());
        return Result.ok();
    }

    /**
     * 取消指定订单。
     *
     * @param userId  当前登录用户ID
     * @param orderNo 订单编号
     * @return 空成功的通用响应
     */
    @PutMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@RequestAttribute Integer userId, @PathVariable String orderNo) {
        orderService.cancel(userId, orderNo);
        return Result.ok();
    }

    /**
     * 签收订单（确认收到商品/服务）。
     *
     * @param userId  当前登录用户ID
     * @param orderNo 订单编号
     * @return 空成功的通用响应
     */
    @PutMapping("/{orderNo}/sign")
    public Result<Void> sign(@RequestAttribute Integer userId, @PathVariable String orderNo) {
        orderService.sign(userId, orderNo);
        return Result.ok();
    }

    /**
     * 确认收货（完成订单）。
     *
     * @param userId  当前登录用户ID
     * @param orderNo 订单编号
     * @return 空成功的通用响应
     */
    @PutMapping("/{orderNo}/confirm-receipt")
    public Result<Void> confirmReceipt(@RequestAttribute Integer userId, @PathVariable String orderNo) {
        orderService.confirmReceipt(userId, orderNo);
        return Result.ok();
    }

    /**
     * 申请退款。
     *
     * @param userId  当前登录用户ID
     * @param orderNo 订单编号
     * @param request 退款请求（退款原因、金额等）
     * @return 空成功的通用响应
     */
    @PostMapping("/{orderNo}/refund")
    public Result<Void> refund(
            @RequestAttribute Integer userId,
            @PathVariable String orderNo,
            @RequestBody RefundRequest request) {
        orderService.requestRefund(userId, orderNo, request);
        return Result.ok();
    }
}
