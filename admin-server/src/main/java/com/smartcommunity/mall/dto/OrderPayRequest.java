package com.smartcommunity.mall.dto;

import lombok.Data;

/** 订单支付请求表单。 */
@Data
public class OrderPayRequest {
    /** QR 为模拟扫码，WALLET 为钱包余额。 */
    private String paymentMethod; // 支付方式（QR扫码 WALLET钱包）
}
