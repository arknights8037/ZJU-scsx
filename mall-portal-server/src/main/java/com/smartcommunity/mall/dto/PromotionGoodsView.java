package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/** 促销商品展示对象，保留原价并补充活动价。 */
@Data
public class PromotionGoodsView {
    private Integer id;
    private String goodsNo;
    private String goodsName;
    private Integer categoryId;
    private String goodsIntroduce;
    private Integer goodsState;
    private String goodsPicture;
    private BigDecimal goodsMarketPrice;
    private BigDecimal promotionPrice;
    private String promotionLabel;
    private String badgeText;
}
