package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 订单实体，对应 orders 表（订单主表）。 */
@Data
@TableName("orders")
public class Orders {
    @TableId(type = IdType.AUTO)
    private Integer id; // 订单ID（主键，自增）
    private String orderNo; // 订单编号
    private Integer userId; // 用户ID
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
