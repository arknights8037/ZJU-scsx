package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("parking")
public class Parking {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private String parkingName;
    private Integer parkingType;
    private Integer parkingStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
