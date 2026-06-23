package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("role_user")
public class RoleUser {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer roleId;
    private Integer userId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
