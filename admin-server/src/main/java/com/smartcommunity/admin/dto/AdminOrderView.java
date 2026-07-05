package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表展示对象。
 *
 * 数据库实体只保存 userId 等关联字段；DTO 额外带上用户、商品和门店名称，
 * 让前端不需要拿着一串编号再逐个请求详情。
 */
@Data
public class AdminOrderView {
    private Integer id; // 订单ID
    private String orderNo; // 订单编号
    private Integer userId; // 用户ID
    private String userName; // 用户名
    private String userPhone; // 用户手机号
    private Double totalPrice; // 订单总金额
    private Integer paymentType; // 支付方式（1微信 2支付宝 3钱包）
    private Integer paymentSubtype; // 支付子类型
    private Integer deliveryType; // 配送方式（1自提 2配送）
    private Integer orderState; // 订单状态
    private BigDecimal refundAmount; // 退款金额
    private String refundReason; // 退款原因
    private String refundDescription; // 退款说明
    private LocalDateTime refundTime; // 退款申请时间
    private LocalDateTime refundHandledTime; // 退款处理时间
    private Integer itemCount; // 商品种类数
    private String goodsSummary; // 商品摘要（商品名拼接）
    private String storeSummary; // 门店摘要（门店名拼接）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
