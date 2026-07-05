package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 居民端可见的有效促销活动。 */
@Data
public class SpecialView {
    private Integer id; // 促销ID
    private String specialName; // 促销名称
    private String specialSubtitle; // 促销副标题
    private String badgeText; // 角标文字
    private String promotionType; // 促销类型（DISCOUNT打折 REDUCE满减）
    private BigDecimal discountValue; // 折扣值/满减金额
    private LocalDateTime startTime; // 促销开始时间
    private LocalDateTime endTime; // 促销结束时间
    private Integer sortOrder; // 排序权重
    private Integer maxItems; // 每人限购数量
}
