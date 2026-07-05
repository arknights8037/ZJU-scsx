package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/** 促销商品展示对象，保留原价并补充活动价。 */
@Data
public class PromotionGoodsView {
    private Integer id; // 商品ID
    private String goodsNo; // 商品编号
    private String goodsName; // 商品名称
    private Integer categoryId; // 分类ID
    private String goodsIntroduce; // 商品简介
    private Integer goodsState; // 商品状态
    private String goodsPicture; // 商品图片URL
    private BigDecimal goodsMarketPrice; // 商品市场价（原价）
    private BigDecimal promotionPrice; // 促销价
    private String promotionLabel; // 促销标签
    private String badgeText; // 角标文字
    private String specialName; // 促销活动名称
}
