package com.smartcommunity.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单列表展示对象。
 *
 * 数据库实体只保存 userId 等关联字段；DTO 额外带上用户、商品和门店名称，
 * 让前端不需要拿着一串编号再逐个请求详情。
 */
@Data
public class AdminOrderView {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private String userName;
    private String userPhone;
    private Double totalPrice;
    private Integer paymentType;
    private Integer paymentSubtype;
    private Integer deliveryType;
    private Integer orderState;
    private Integer itemCount;
    private String goodsSummary;
    private String storeSummary;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
