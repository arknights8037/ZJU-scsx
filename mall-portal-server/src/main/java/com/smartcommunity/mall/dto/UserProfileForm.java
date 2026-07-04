package com.smartcommunity.mall.dto;

import lombok.Data;

/**
 * 个人资料修改表单。
 *
 * 这里只放允许居民自己修改的字段，手机号、用户状态等敏感字段不放进来。
 */
@Data
public class UserProfileForm {

    private String userName;
    private String sex;
    private String mail;
    private String avatar;
}
