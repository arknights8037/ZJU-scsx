package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 特殊促销板块
 */
@Data
@TableName("special")
public class Special {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String specialName;
    private Integer specialStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
