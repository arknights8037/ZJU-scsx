package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 居民端收藏商品展示对象。
 *
 * 收藏表只保存 goodsNo/storeNo，这个 DTO 额外带商品名称、图片、价格和库存，
 * 前端可以直接渲染收藏卡片。
 */
@Data
public class FavoriteGoodsView {
    private Integer id; // 收藏记录ID
    private String goodsNo; // 商品编号
    private String storeNo; // 门店编号
    private String goodsName; // 商品名称
    private String goodsPicture; // 商品图片URL
    private BigDecimal goodsMarketPrice; // 商品市场价（原价）
    private BigDecimal promotionPrice; // 促销价
    private String promotionLabel; // 促销标签（如"限时特惠"）
    private String badgeText; // 角标文字
    private String specialName; // 促销活动名称
    private BigDecimal goodsPrice; // 门店售价
    private Integer goodsStock; // 门店库存
    private Integer goodsState; // 商品状态
    private LocalDateTime createTime; // 收藏时间
}
