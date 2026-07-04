package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 促销管理页展示对象，包含规则和已选商品数量。 */
@Data
public class AdminSpecialView {
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
    private Integer goodsCount;
    private String effectiveStatus;
    private List<String> goodsNos;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
