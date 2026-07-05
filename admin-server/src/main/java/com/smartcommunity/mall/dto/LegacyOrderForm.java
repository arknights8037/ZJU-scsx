package com.smartcommunity.mall.dto;

import com.smartcommunity.mall.entity.Carts;
import lombok.Data;

import java.util.List;

/** 老订单查询表单（兼容旧版订单查询接口）。 */
@Data
public class LegacyOrderForm {

    private Integer userId; // 用户ID
    private Double totalPrice; // 订单总金额
    private Integer year; // 查询年份
    private String goodsName; // 商品名称（模糊查询）
    private List<Carts> records; // 购物车记录列表（下单商品）
}
