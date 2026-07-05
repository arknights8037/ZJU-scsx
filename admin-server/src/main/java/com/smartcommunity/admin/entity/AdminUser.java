package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 后台管理员用户实体，对应 user 表（登录账号信息）。 */
@Data
@TableName("user")
public class AdminUser {
    @TableId(type = IdType.AUTO)
    private Integer id; // 用户ID（主键，自增）
    private String phone; // 手机号（登录账号）
    private String userName; // 用户昵称
    private String userPassword; // 登录密码
    private String sex; // 性别（0未知 1男 2女）
    private String mail; // 邮箱
    private String avatar; // 头像URL
    private LocalDateTime lastLoginTime; // 最后登录时间
    private Integer userStatus; // 用户状态（0禁用 1正常）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
