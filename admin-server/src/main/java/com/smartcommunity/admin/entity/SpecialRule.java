package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 促销扩展规则实体，对应 special_rule 表（与 special 表一对一关联）。 */
@Data
@TableName("special_rule")
public class SpecialRule {
    @TableId(type = IdType.AUTO)
    private Integer id; // 规则ID（主键，自增）
    private Integer specialId; // 促销活动ID
    private String specialSubtitle; // 促销副标题
    private String badgeText; // 角标文字
    private String promotionType; // 促销类型（DISCOUNT打折 REDUCE满减）
    private BigDecimal discountValue; // 折扣值/满减金额
    private LocalDateTime startTime; // 促销开始时间
    private LocalDateTime endTime; // 促销结束时间
    private Integer sortOrder; // 排序权重
    private Integer maxItems; // 每人限购数量
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
