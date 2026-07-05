package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 角色实体，对应 role 表（后台管理角色）。 */
@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Integer id; // 角色ID（主键，自增）
    private String roleName; // 角色名称
    private String roleDesc; // 角色描述
    private Integer roleState; // 角色状态（0禁用 1启用）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
