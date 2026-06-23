package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
@TableName("orders")
public class Orders {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String orderNo;
    private Integer userId;
    private Double totalPrice;
    private Integer paymentType;
    private Integer paymentSubtype;
    private Integer deliveryType;
    private Integer orderState;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
