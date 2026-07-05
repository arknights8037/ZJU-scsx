package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.dto.WalletRechargeRequest;
import com.smartcommunity.mall.dto.WalletTransferRequest;
import com.smartcommunity.mall.dto.WalletView;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.entity.Wallet;
import com.smartcommunity.mall.entity.WalletTransaction;
import com.smartcommunity.mall.mapper.UserMapper;
import com.smartcommunity.mall.mapper.WalletMapper;
import com.smartcommunity.mall.mapper.WalletTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/** 居民虚拟钱包：余额和流水均保存到数据库，不接入真实支付渠道。 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private static final BigDecimal MAX_SINGLE_AMOUNT = new BigDecimal("50000.00");

    private final WalletMapper walletMapper;
    private final WalletTransactionMapper transactionMapper;
    private final UserMapper userMapper;

    /**
     * 获取用户钱包信息（余额等）。
     * 如果钱包不存在则自动创建。
     *
     * @param userId 用户 ID
     * @return 钱包视图（含余额）
     */
    @Transactional
    public WalletView getWallet(Integer userId) {
        return toView(ensureWallet(userId));
    }

    /**
     * 分页查询用户钱包交易流水。
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页条数（最大 50）
     * @return 交易流水分页结果
     */
    public Page<WalletTransaction> transactions(Integer userId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return transactionMapper.selectPage(new Page<>(safePage, safeSize),
                new LambdaQueryWrapper<WalletTransaction>()
                        .eq(WalletTransaction::getUserId, userId)
                        .orderByDesc(WalletTransaction::getCreateTime)
                        .orderByDesc(WalletTransaction::getId));
    }

    /**
     * 用户钱包充值（虚拟充值，不接入真实支付渠道）。
     *
     * @param userId  用户 ID
     * @param request 充值请求（含金额和渠道）
     * @return 充值后的钱包视图
     */
    @Transactional
    public WalletView recharge(Integer userId, WalletRechargeRequest request) {
        BigDecimal amount = normalizeAmount(request == null ? null : request.getAmount());
        ensureWallet(userId);
        Wallet wallet = lockWallet(userId);
        BigDecimal balance = balance(wallet).add(amount);
        wallet.setWalletBalance(balance);
        walletMapper.updateById(wallet);

        String channel = request == null ? null : request.getChannel();
        saveTransaction(transactionNo(), userId, "RECHARGE", amount, balance,
                null, null, StringUtils.hasText(channel) ? "虚拟充值 · " + channel.trim() : "虚拟充值");
        return toView(wallet);
    }

    @Transactional
    public WalletView transfer(Integer userId, WalletTransferRequest request) {
        if (request == null || !StringUtils.hasText(request.getRecipientPhone())) {
            throw new RuntimeException("请输入收款人手机号");
        }
        BigDecimal amount = normalizeAmount(request.getAmount());
        User sender = requireUser(userId);
        User recipient = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getRecipientPhone().trim())
                .last("limit 1"));
        if (recipient == null) throw new RuntimeException("收款用户不存在");
        if (recipient.getId().equals(userId)) throw new RuntimeException("不能转账给自己");

        ensureWallet(userId);
        ensureWallet(recipient.getId());

        // 固定按用户ID顺序加锁，避免两位用户同时互相转账时形成死锁。
        Wallet first = lockWallet(Math.min(userId, recipient.getId()));
        Wallet second = lockWallet(Math.max(userId, recipient.getId()));
        Wallet senderWallet = first.getUserId().equals(userId) ? first : second;
        Wallet recipientWallet = first.getUserId().equals(recipient.getId()) ? first : second;

        if (balance(senderWallet).compareTo(amount) < 0) throw new RuntimeException("钱包余额不足");
        BigDecimal senderBalance = balance(senderWallet).subtract(amount);
        BigDecimal recipientBalance = balance(recipientWallet).add(amount);
        senderWallet.setWalletBalance(senderBalance);
        recipientWallet.setWalletBalance(recipientBalance);
        walletMapper.updateById(senderWallet);
        walletMapper.updateById(recipientWallet);

        String transactionNo = transactionNo();
        String remark = request.getRemark() == null ? null : request.getRemark().trim();
        saveTransaction(transactionNo, userId, "TRANSFER_OUT", amount.negate(), senderBalance,
                recipient.getId(), displayName(recipient), text(remark, "转账支出"));
        saveTransaction(transactionNo, recipient.getId(), "TRANSFER_IN", amount, recipientBalance,
                sender.getId(), displayName(sender), text(remark, "转账收入"));
        return toView(senderWallet);
    }

    /** 商城订单余额支付，加入调用方事务后与订单状态保持原子性。 */
    @Transactional
    public WalletView payForOrder(Integer userId, BigDecimal amountValue, String orderNo) {
        BigDecimal amount = normalizeAmount(amountValue);
        ensureWallet(userId);
        Wallet wallet = lockWallet(userId);
        if (balance(wallet).compareTo(amount) < 0) throw new RuntimeException("钱包余额不足，请充值后再支付");
        BigDecimal newBalance = balance(wallet).subtract(amount);
        wallet.setWalletBalance(newBalance);
        walletMapper.updateById(wallet);
        saveTransaction(transactionNo(), userId, "ORDER_PAYMENT", amount.negate(), newBalance,
                null, "社区商城", "订单支付 · " + orderNo);
        return toView(wallet);
    }

    /** 钱包支付订单退款成功后原路退回虚拟余额。 */
    @Transactional
    public WalletView refundOrder(Integer userId, BigDecimal amountValue, String orderNo) {
        BigDecimal amount = normalizeAmount(amountValue);
        ensureWallet(userId);
        Wallet wallet = lockWallet(userId);
        BigDecimal newBalance = balance(wallet).add(amount);
        wallet.setWalletBalance(newBalance);
        walletMapper.updateById(wallet);
        saveTransaction(transactionNo(), userId, "ORDER_REFUND", amount, newBalance,
                null, "社区商城", "订单退款 · " + orderNo);
        return toView(wallet);
    }

    /**
     * 确保用户钱包存在（不存在则自动创建，初始余额为 0）。
     * 并发时借助数据库唯一索引防止重复创建。
     *
     * @param userId 用户 ID
     * @return 钱包对象
     */
    private Wallet ensureWallet(Integer userId) {
        Wallet existing = findWallet(userId);
        if (existing != null) return existing;
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setWalletBalance(BigDecimal.ZERO.setScale(2));
        try {
            walletMapper.insert(wallet);
            return wallet;
        } catch (DuplicateKeyException ignored) {
            return findWallet(userId);
        }
    }

    /**
     * 查询用户钱包。
     *
     * @param userId 用户 ID
     * @return 钱包对象（可能为 null）
     */
    private Wallet findWallet(Integer userId) {
        return walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUserId, userId)
                .last("limit 1"));
    }

    /**
     * 锁住用户钱包行记录（select for update），用于事务中防止并发修改。
     *
     * @param userId 用户 ID
     * @return 钱包对象
     */
    private Wallet lockWallet(Integer userId) {
        Wallet wallet = walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUserId, userId)
                .last("limit 1 for update"));
        if (wallet == null) throw new RuntimeException("钱包初始化失败");
        return wallet;
    }

    /**
     * 根据用户 ID 获取用户，不存在则抛异常。
     *
     * @param userId 用户 ID
     * @return User 对象
     */
    private User requireUser(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        return user;
    }

    /**
     * 校验并标准化金额：大于 0、不超过单笔上限（5 万元）、保留两位小数。
     *
     * @param value 原始金额
     * @return 标准化后的金额
     */
    private BigDecimal normalizeAmount(BigDecimal value) {
        if (value == null) throw new RuntimeException("请输入金额");
        BigDecimal amount = value.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("金额必须大于0元");
        if (amount.compareTo(MAX_SINGLE_AMOUNT) > 0) throw new RuntimeException("单笔金额不能超过50000元");
        return amount;
    }

    /**
     * 保存一笔交易流水记录。
     *
     * @param transactionNo   交易编号
     * @param userId          用户 ID
     * @param type            交易类型（RECHARGE/TRANSFER_OUT/TRANSFER_IN/ORDER_PAYMENT/ORDER_REFUND）
     * @param amount          交易金额
     * @param balanceAfter    交易后余额
     * @param counterpartyId  对方用户 ID
     * @param counterpartyName 对方用户名
     * @param remark          备注
     */
    private void saveTransaction(String transactionNo, Integer userId, String type,
                                 BigDecimal amount, BigDecimal balanceAfter,
                                 Integer counterpartyId, String counterpartyName, String remark) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionNo(transactionNo);
        transaction.setUserId(userId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setCounterpartyUserId(counterpartyId);
        transaction.setCounterpartyName(counterpartyName);
        transaction.setRemark(remark);
        transactionMapper.insert(transaction);
    }

    /**
     * 将钱包实体转换为前端视图（含格式化后的账户号和余额）。
     *
     * @param wallet 钱包实体
     * @return 钱包视图
     */
    private WalletView toView(Wallet wallet) {
        return new WalletView("SCW" + String.format("%08d", wallet.getUserId()), balance(wallet));
    }

    /**
     * 获取钱包余额，null 安全且统一保留两位小数。
     *
     * @param wallet 钱包实体
     * @return 余额（保留两位小数）
     */
    private BigDecimal balance(Wallet wallet) {
        return wallet.getWalletBalance() == null
                ? BigDecimal.ZERO.setScale(2)
                : wallet.getWalletBalance().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取用户展示名，优先使用用户名，其次使用手机号。
     *
     * @param user 用户对象
     * @return 展示名称
     */
    private String displayName(User user) {
        return StringUtils.hasText(user.getUserName()) ? user.getUserName() : user.getPhone();
    }

    /**
     * 取第一个非空字符串，为空时使用 fallback。
     *
     * @param value    首选值
     * @param fallback 备选值
     * @return 非空字符串
     */
    private String text(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    /**
     * 生成唯一交易编号（WT + UUID）。
     *
     * @return 交易编号字符串
     */
    private String transactionNo() {
        return "WT" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
