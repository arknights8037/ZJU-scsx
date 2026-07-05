package com.smartcommunity.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/** 个人消费汇总统计。 */
@Data
@AllArgsConstructor
public class PersonalExpenseSummary {
    private BigDecimal totalExpense; // 总消费金额
    private BigDecimal monthExpense; // 本月消费金额
    private long recordCount; // 消费记录条数
}
