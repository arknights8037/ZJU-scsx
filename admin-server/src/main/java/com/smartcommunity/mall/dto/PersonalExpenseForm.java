package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 个人消费记账表单。 */
@Data
public class PersonalExpenseForm {
    private String category; // 消费类别（如"餐饮"、"交通"）
    private String expenseTitle; // 消费标题
    private BigDecimal amount; // 消费金额
    private LocalDateTime expenseTime; // 消费时间
    private String remark; // 备注
}
