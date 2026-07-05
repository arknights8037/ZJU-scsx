package com.smartcommunity.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门店列表展示对象，补充区域名称。
 */
@Data
public class AdminStoreView {
    private Integer id; // 门店ID
    private Integer areaId; // 所属区域ID
    private String areaName; // 区域名称
    private String storeNo; // 门店编号
    private String storeName; // 门店名称
    private String storeAddress; // 门店地址
    private Double longitude; // 经度
    private Double latitude; // 纬度
    private String storeIntroduce; // 门店简介
    private LocalDateTime startTime; // 营业开始时间
    private LocalDateTime closeTime; // 营业结束时间
    private Integer storeStatus; // 门店状态（0关闭 1营业）
    private Long goodsCount; // 商品数量
    private Long totalStock; // 总库存
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
