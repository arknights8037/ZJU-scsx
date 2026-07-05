package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 居民住户扩展资料实体，对应 user_resident_profile 表。
 *
 * 老项目的 user 表已经承担登录账号职责，住址、门牌号等社区业务字段放在扩展表里，
 * 避免破坏原始用户表结构。
 */
@Data
@TableName("user_resident_profile")
public class UserResidentProfile {

    @TableId(type = IdType.AUTO)
    private Integer id; // 资料ID（主键，自增）

    private Integer userId; // 用户ID
    private String ownerName; // 业主姓名
    private String buildingNo; // 楼栋号
    private String unitNo; // 单元号
    private String roomNo; // 门牌号
    private String fullAddress; // 完整地址
    private String emergencyContact; // 紧急联系人
    private String emergencyPhone; // 紧急联系电话

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
