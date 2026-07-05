package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 缴费账单实体，对应 charge 表（物业费、水电费等）。 */
@Data
@TableName("charge")
public class Charge {
    @TableId(type = IdType.AUTO)
    private Integer id; // 账单ID（主键，自增）
    private String chargeNo; // 账单编号
    private Integer userId; // 所属用户ID
    private String chargeName; // 账单名称（如"2024年1月物业费"）
    private Integer chargeStatus; // 缴费状态（0未缴 1已缴 2已逾期）
    private Double totalPrice; // 账单金额
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
