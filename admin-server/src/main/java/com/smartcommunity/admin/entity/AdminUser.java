package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class AdminUser {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String phone;
    private String userName;
    private String userPassword;
    private String sex;
    private String mail;
    private String avatar;
    private LocalDateTime lastLoginTime;
    private Integer userStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
