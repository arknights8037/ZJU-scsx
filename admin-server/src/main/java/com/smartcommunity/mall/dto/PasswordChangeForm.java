package com.smartcommunity.mall.dto;

import lombok.Data;

/** 修改密码表单。前端传原密码和新密码，后端负责校验原密码。 */
@Data
public class PasswordChangeForm {

    private String oldPassword; // 原密码
    private String newPassword; // 新密码
}
