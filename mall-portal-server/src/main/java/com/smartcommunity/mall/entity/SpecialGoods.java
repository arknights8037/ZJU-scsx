package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 特殊板块商品关联
 */
@Data
@TableName("special_goods")
public class SpecialGoods {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer specialId;
    private String goodsNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
