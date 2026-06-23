package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
@TableName("carts")
public class Carts {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private String storeNo;
    private String goodsNo;
    private Integer amount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
