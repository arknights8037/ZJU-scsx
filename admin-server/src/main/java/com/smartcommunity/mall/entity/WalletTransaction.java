package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 虚拟钱包资金流水实体，对应 wallet_transaction 表（金额带正负号并记录交易后的余额快照）。 */
@Data
@TableName("wallet_transaction")
public class WalletTransaction {
    @TableId(type = IdType.AUTO)
    private Long id; // 流水ID（主键，自增）
    private String transactionNo; // 交易流水号
    private Integer userId; // 用户ID
    private String transactionType; // 交易类型（RECHARGE充值 PAYMENT消费 TRANSFER转账 REFUND退款）
    private BigDecimal amount; // 交易金额（正为收入，负为支出）
    private BigDecimal balanceAfter; // 交易后余额
    private Integer counterpartyUserId; // 交易对方用户ID
    private String counterpartyName; // 交易对方名称
    private String remark; // 交易备注
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
}
