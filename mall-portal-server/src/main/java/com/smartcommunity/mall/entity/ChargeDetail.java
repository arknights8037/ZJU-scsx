package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("charge_detail")
public class ChargeDetail {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String chargeNo;
    private String chargeContent;
    private Double chargePrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
