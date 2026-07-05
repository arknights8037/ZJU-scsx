package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 门店售卖商品视图：把 goods_store 关系补充成后台可读信息。 */
@Data
public class AdminStoreGoodsView {
    private Integer id; // 关联记录ID
    private String storeNo; // 门店编号
    private String storeName; // 门店名称
    private String goodsNo; // 商品编号
    private String goodsName; // 商品名称
    private String goodsPicture; // 商品图片URL
    private String categoryName; // 分类名称（中文）
    private Integer goodsState; // 商品状态
    private BigDecimal goodsMarketPrice; // 商品市场价（原价）
    private BigDecimal goodsPrice; // 门店售价
    private Integer goodsStock; // 门店库存
    private Integer goodsType; // 商品类型
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
