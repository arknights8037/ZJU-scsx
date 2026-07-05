package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 个人消费账单实体，对应 personal_expense 表（与钱包资金流水相互独立）。 */
@Data
@TableName("personal_expense")
public class PersonalExpense {
    @TableId(type = IdType.AUTO)
    private Long id; // 账单ID（主键，自增）
    private String expenseNo; // 账单编号
    private Integer userId; // 用户ID
    private String sourceType; // 来源类型（如"manual"手动录入 "order"订单同步）
    private String sourceNo; // 来源单号
    private String category; // 消费类别
    private String expenseTitle; // 消费标题
    private BigDecimal amount; // 消费金额
    private LocalDateTime expenseTime; // 消费时间
    private String remark; // 备注
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
