package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 商品图片实体，对应 goods_picture 表（商品的多张图片）。 */
@Data
@TableName("goods_picture")
public class GoodsPicture {

    @TableId(type = IdType.AUTO)
    private Integer id; // 图片ID（主键，自增）

    private String goodsNo; // 商品编号
    private String pictureUrl; // 图片URL
    private Integer pictureType; // 图片类型（0主图 1详情图）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
