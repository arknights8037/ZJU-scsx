package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 促销管理页展示对象，包含规则和已选商品数量。 */
@Data
public class AdminSpecialView {
    private Integer id; // 促销ID
    private String specialName; // 促销名称
    private String specialSubtitle; // 促销副标题
    private String badgeText; // 角标文字
    private Integer specialStatus; // 促销状态（0关闭 1开启）
    private String promotionType; // 促销类型（DISCOUNT打折 REDUCE满减）
    private BigDecimal discountValue; // 折扣值/满减金额
    private LocalDateTime startTime; // 促销开始时间
    private LocalDateTime endTime; // 促销结束时间
    private Integer sortOrder; // 排序权重
    private Integer maxItems; // 每人限购数量
    private Integer goodsCount; // 参与商品数量
    private String effectiveStatus; // 生效状态（进行中/未开始/已结束）
    private List<String> goodsNos; // 参与商品编号列表
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
