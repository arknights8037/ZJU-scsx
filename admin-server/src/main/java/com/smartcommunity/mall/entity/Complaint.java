package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 投诉建议实体，对应 complaint 表（业主投诉与建议）。 */
@Data
@TableName("complaint")
public class Complaint {

    @TableId(type = IdType.AUTO)
    private Integer id; // 投诉ID（主键，自增）

    private Integer userId; // 投诉人用户ID
    private String complaintContent; // 投诉内容
    private Integer complaintStatus; // 处理状态（0待处理 1处理中 2已处理）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
