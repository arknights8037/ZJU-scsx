package com.smartcommunity.mall.dto;

import lombok.Data;

/**
 * 个人资料修改表单。
 *
 * 这里只放允许居民自己修改的字段，手机号、用户状态等敏感字段不放进来。
 */
@Data
public class UserProfileForm {

    private String userName; // 用户昵称
    private String sex; // 性别（0未知 1男 2女）
    private String mail; // 邮箱
    private String avatar; // 头像URL
    private String ownerName; // 业主姓名
    private String buildingNo; // 楼栋号
    private String unitNo; // 单元号
    private String roomNo; // 门牌号
    private String fullAddress; // 完整地址
    private String emergencyContact; // 紧急联系人
    private String emergencyPhone; // 紧急联系电话
}
