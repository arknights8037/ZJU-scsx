package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("charge")
public class Charge {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String chargeNo;
    private Integer userId;
    private String chargeName;
    private Integer chargeStatus;
    private Double totalPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
