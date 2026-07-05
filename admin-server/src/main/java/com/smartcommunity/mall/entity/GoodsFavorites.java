package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 商品收藏实体，对应 goods_favorites 表（用户收藏的商品记录）。 */
@Data
@TableName("goods_favorites")
public class GoodsFavorites {

    @TableId(type = IdType.AUTO)
    private Integer id; // 收藏ID（主键，自增）

    private Integer userId; // 用户ID
    private String goodsNo; // 商品编号
    private String storeNo; // 门店编号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 收藏时间
}
