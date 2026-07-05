package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/** 钱包充值请求表单。 */
@Data
public class WalletRechargeRequest {
    private BigDecimal amount; // 充值金额
    private String channel; // 充值渠道（如"wechat"、"alipay"）
}
