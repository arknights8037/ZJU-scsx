package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 促销的时间、折扣和展示规则。 */
@Data
@TableName("special_rule")
public class SpecialRule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer specialId;
    private String specialSubtitle;
    private String badgeText;
    private String promotionType;
    private BigDecimal discountValue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer sortOrder;
    private Integer maxItems;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
