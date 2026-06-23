package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 钱包
 */
@Data
@TableName("wallet")
public class Wallet {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Double walletBalance;
    private String walletPassword;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
