package com.smartcommunity.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 后台保存促销时提交的完整配置。 */
@Data
public class SpecialSaveRequest {
    private Integer id; // 促销ID（新增时为null，编辑时必填）
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
    private List<String> goodsNos; // 参与商品编号列表
}
