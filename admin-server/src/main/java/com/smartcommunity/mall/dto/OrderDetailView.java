package com.smartcommunity.mall.dto;

import lombok.Data;

/**
 * 居民端订单明细展示对象。
 *
 * 新订单会在 order_detail 里保存商品名、商品图和门店名快照；旧订单没有快照时，
 * 服务层再按 goodsNo/storeNo 尝试补齐现有商品和门店资料。
 */
@Data
public class OrderDetailView {
    private Integer id; // 明细ID
    private String orderNo; // 订单编号
    private String storeNo; // 门店编号
    private String storeName; // 门店名称
    private String goodsNo; // 商品编号
    private String goodsName; // 商品名称
    private String goodsPicture; // 商品图片URL
    private Integer goodsAmount; // 商品数量
    private Double goodsPrice; // 商品单价
    private Double totalPrice; // 该商品小计金额
}
