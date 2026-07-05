package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 钱包实体，对应 wallet 表（居民端虚拟钱包）。 */
@Data
@TableName("wallet")
public class Wallet {

    @TableId(type = IdType.AUTO)
    private Integer id; // 钱包ID（主键，自增）

    private Integer userId; // 用户ID
    private BigDecimal walletBalance; // 钱包余额
    private String walletPassword; // 钱包支付密码

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
