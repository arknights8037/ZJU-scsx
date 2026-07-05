package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 促销活动实体，对应 special 表（促销活动主表）。 */
@Data
@TableName("special")
public class Special {

    @TableId(type = IdType.AUTO)
    private Integer id; // 促销ID（主键，自增）

    private String specialName; // 促销名称
    private Integer specialStatus; // 促销状态（0关闭 1开启）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
