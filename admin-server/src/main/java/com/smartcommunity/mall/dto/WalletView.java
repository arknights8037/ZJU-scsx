package com.smartcommunity.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/** 钱包信息展示对象。 */
@Data
@AllArgsConstructor
public class WalletView {
    private String walletNo; // 钱包编号
    private BigDecimal balance; // 钱包余额
}
