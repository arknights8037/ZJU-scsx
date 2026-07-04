package com.smartcommunity.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门店列表展示对象，补充区域名称。
 */
@Data
public class AdminStoreView {
    private Integer id;
    private Integer areaId;
    private String areaName;
    private String storeNo;
    private String storeName;
    private String storeAddress;
    private Double longitude;
    private Double latitude;
    private String storeIntroduce;
    private LocalDateTime startTime;
    private LocalDateTime closeTime;
    private Integer storeStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
