package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 居民端退款申请表单。
 *
 * 退款金额允许前端填写；为空时默认按订单实付金额整单退款。
 */
@Data
public class RefundRequest {
    private BigDecimal refundAmount; // 退款金额（为空则整单退款）
    private String refundReason; // 退款原因
    private String refundDescription; // 退款说明
}
