package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 商品实体，对应 goods 表（商品基本信息）。 */
@Data
@TableName("goods")
public class Goods {

    @TableId(type = IdType.AUTO)
    private Integer id; // 商品ID（主键，自增）

    private String goodsNo; // 商品编号
    private String goodsName; // 商品名称
    private Integer categoryId; // 分类ID
    private String goodsIntroduce; // 商品简介
    private String goodsContent; // 商品详情内容
    private Integer goodsState; // 商品状态（0下架 1上架）
    private String goodsPicture; // 商品图片URL
    private BigDecimal goodsMarketPrice; // 商品市场价（原价）

    @TableField(exist = false)
    private BigDecimal promotionPrice; // 促销价（非数据库字段）
    @TableField(exist = false)
    private String promotionLabel; // 促销标签（非数据库字段）
    @TableField(exist = false)
    private String badgeText; // 角标文字（非数据库字段）
    @TableField(exist = false)
    private String specialName; // 促销活动名称（非数据库字段）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
