package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 商品管理列表展示对象，额外携带中文分类名称。 */
@Data
public class AdminGoodsView {
    private Integer id;
    private String goodsNo;
    private String goodsName;
    private Integer categoryId;
    private String categoryName;
    private String goodsIntroduce;
    private String goodsContent;
    private Integer goodsState;
    private String goodsPicture;
    private BigDecimal goodsMarketPrice;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
