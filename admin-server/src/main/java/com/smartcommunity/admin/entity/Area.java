package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("area")
public class Area {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String areaNo;
    private String areaName;
    private Integer parentId;
    private Integer areaType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
