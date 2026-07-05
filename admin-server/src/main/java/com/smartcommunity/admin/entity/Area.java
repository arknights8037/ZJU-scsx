package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 区域实体，对应 area 表（用于小区、楼栋等地理区域划分）。 */
@Data
@TableName("area")
public class Area {
    @TableId(type = IdType.AUTO)
    private Integer id; // 区域ID（主键，自增）
    private String areaNo; // 区域编号
    private String areaName; // 区域名称
    private Integer parentId; // 父级区域ID
    private Integer areaType; // 区域类型（1小区 2楼栋 3单元）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
