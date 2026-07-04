package com.smartcommunity.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 居民端可见的有效促销活动。 */
@Data
public class SpecialView {
    private Integer id;
    private String specialName;
    private String specialSubtitle;
    private String badgeText;
    private String promotionType;
    private BigDecimal discountValue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer sortOrder;
    private Integer maxItems;
}
