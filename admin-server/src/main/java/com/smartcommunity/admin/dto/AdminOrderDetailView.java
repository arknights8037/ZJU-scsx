package com.smartcommunity.admin.dto;

import lombok.Data;

/**
 * 订单明细展示对象，把商品编号和门店编号翻译为人类可读名称。
 */
@Data
public class AdminOrderDetailView {
    private Integer id;
    private String orderNo;
    private String storeNo;
    private String storeName;
    private String goodsNo;
    private String goodsName;
    private String goodsPicture;
    private Integer goodsAmount;
    private Double goodsPrice;
    private Double totalPrice;
}
