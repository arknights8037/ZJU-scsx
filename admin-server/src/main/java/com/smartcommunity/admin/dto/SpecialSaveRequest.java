package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 后台保存促销时提交的完整配置。 */
@Data
public class SpecialSaveRequest {
    private Integer id;
    private String specialName;
    private String specialSubtitle;
    private String badgeText;
    private Integer specialStatus;
    private String promotionType;
    private BigDecimal discountValue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer sortOrder;
    private Integer maxItems;
    private List<String> goodsNos;
}
