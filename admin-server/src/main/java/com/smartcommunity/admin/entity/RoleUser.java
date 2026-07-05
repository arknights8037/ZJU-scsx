package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 角色-用户关联实体，对应 role_user 表（用户与角色的多对多关系）。 */
@Data
@TableName("role_user")
public class RoleUser {
    @TableId(type = IdType.AUTO)
    private Integer id; // 关联ID（主键，自增）
    private Integer roleId; // 角色ID
    private Integer userId; // 用户ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
