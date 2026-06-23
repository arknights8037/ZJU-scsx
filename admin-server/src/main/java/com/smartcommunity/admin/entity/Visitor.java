package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("visitor")
public class Visitor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String visitorObjective;
    private LocalDateTime visitorTime;
    private Integer visitorStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
