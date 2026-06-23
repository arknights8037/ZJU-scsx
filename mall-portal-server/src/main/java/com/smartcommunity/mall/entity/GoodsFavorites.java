package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品收藏
 */
@Data
@TableName("goods_favorites")
public class GoodsFavorites {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private String goodsNo;
    private String storeNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
