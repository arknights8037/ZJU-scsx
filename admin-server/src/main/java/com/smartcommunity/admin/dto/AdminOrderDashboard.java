package com.smartcommunity.admin.dto;

import lombok.Data;

/**
 * 订单管理页顶部的轻量统计。
 *
 * 和分页列表共用同一套筛选条件，保证后台看到的仪表盘与当前搜索范围一致。
 */
@Data
public class AdminOrderDashboard {
    private Long totalOrders; // 订单总数
    private Double totalAmount; // 订单总金额
    private Long pendingPaymentCount; // 待付款订单数
    private Long paidCount; // 已付款订单数
    private Long shippingCount; // 配送中订单数
    private Long signedCount; // 已签收订单数
    private Long completedCount; // 已完成订单数
    private Long refundingCount; // 退款中订单数
    private Long refundedCount; // 已退款订单数
    private Long refundRejectedCount; // 退款拒绝订单数
    private Long closedCount; // 已关闭订单数
    private Long canceledCount; // 已取消订单数
}
