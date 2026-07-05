package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 缴费账单明细实体，对应 charge_detail 表（一条账单可包含多项费用明细）。 */
@Data
@TableName("charge_detail")
public class ChargeDetail {
    @TableId(type = IdType.AUTO)
    private Integer id; // 明细ID（主键，自增）
    private String chargeNo; // 关联账单编号
    private String chargeContent; // 费用项目名称（如"水费"、"电费"）
    private Double chargePrice; // 该项费用金额
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
