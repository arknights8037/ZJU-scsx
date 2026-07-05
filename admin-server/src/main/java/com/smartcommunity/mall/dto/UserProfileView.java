package com.smartcommunity.mall.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 居民端个人中心展示对象：账号资料 + 住户资料。
 */
@Data
public class UserProfileView {
    private Integer id; // 用户ID
    private String phone; // 手机号
    private String userName; // 用户昵称
    private String sex; // 性别（0未知 1男 2女）
    private String mail; // 邮箱
    private String avatar; // 头像URL
    private Integer userStatus; // 用户状态（0禁用 1正常）
    private LocalDateTime lastLoginTime; // 最后登录时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    private String ownerName; // 业主姓名
    private String buildingNo; // 楼栋号
    private String unitNo; // 单元号
    private String roomNo; // 门牌号
    private String fullAddress; // 完整地址
    private String emergencyContact; // 紧急联系人
    private String emergencyPhone; // 紧急联系电话
}
