package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/** 钱包转账请求表单。 */
@Data
public class WalletTransferRequest {
    private String recipientPhone; // 收款方手机号
    private BigDecimal amount; // 转账金额
    private String remark; // 转账备注
}
