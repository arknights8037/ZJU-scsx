package com.smartcommunity.admin.dto;

import lombok.Data;

/**
 * 订单明细展示对象，把商品编号和门店编号翻译为人类可读名称。
 */
@Data
public class AdminOrderDetailView {
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
