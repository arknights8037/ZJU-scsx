package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 商品管理列表展示对象，额外携带中文分类名称。 */
@Data
public class AdminGoodsView {
    private Integer id; // 商品ID
    private String goodsNo; // 商品编号
    private String goodsName; // 商品名称
    private Integer categoryId; // 分类ID
    private String categoryName; // 分类名称（中文）
    private String goodsIntroduce; // 商品简介
    private String goodsContent; // 商品详情内容
    private Integer goodsState; // 商品状态（0下架 1上架）
    private String goodsPicture; // 商品图片URL
    private BigDecimal goodsMarketPrice; // 商品市场价（原价）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
