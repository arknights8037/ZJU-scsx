package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品图片
 */
@Data
@TableName("goods_picture")
public class GoodsPicture {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String goodsNo;
    private String pictureUrl;
    private Integer pictureType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
