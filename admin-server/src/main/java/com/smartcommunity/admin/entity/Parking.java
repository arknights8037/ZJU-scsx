package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 车位实体，对应 parking 表（小区车位信息）。 */
@Data
@TableName("parking")
public class Parking {
    @TableId(type = IdType.AUTO)
    private Integer id; // 车位ID（主键，自增）
    private Integer userId; // 业主用户ID
    private String parkingName; // 车位名称/编号
    private Integer parkingType; // 车位类型（0固定车位 1临时车位）
    private Integer parkingStatus; // 车位状态（0空闲 1已用 2维修中）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
