package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单明细
 */
@Data
@TableName("order_detail")
public class OrderDetail {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String orderNo;
    private String storeNo;
    private String goodsNo;
    private Integer goodsAmount;
    private Double goodsPrice;
    private Double totalPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
