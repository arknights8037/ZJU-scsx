package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.WalletRechargeRequest;
import com.smartcommunity.mall.dto.WalletTransferRequest;
import com.smartcommunity.mall.dto.WalletView;
import com.smartcommunity.mall.entity.WalletTransaction;
import com.smartcommunity.mall.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 钱包控制器，提供钱包查询、交易记录查询、充值、转账功能。
 * 请求路径前缀：/api/wallet
 */
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * 获取当前用户的钱包信息（余额等）。
     *
     * @param userId 当前登录用户ID
     * @return 钱包视图的通用响应
     */
    @GetMapping
    public Result<WalletView> wallet(@RequestAttribute Integer userId) {
        return Result.ok(walletService.getWallet(userId));
    }

    /**
     * 获取当前用户的交易记录（分页）。
     *
     * @param userId 当前登录用户ID
     * @param page   页码，默认第1页
     * @param size   每页条数，默认10条
     * @return 交易记录分页的通用响应
     */
    @GetMapping("/transactions")
    public Result<Page<WalletTransaction>> transactions(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(walletService.transactions(userId, page, size));
    }

    /**
     * 钱包充值。
     *
     * @param userId  当前登录用户ID
     * @param request 充值请求（金额、支付方式等）
     * @return 更新后钱包视图的通用响应
     */
    @PostMapping("/recharge")
    public Result<WalletView> recharge(
            @RequestAttribute Integer userId,
            @RequestBody WalletRechargeRequest request) {
        return Result.ok(walletService.recharge(userId, request));
    }

    /**
     * 钱包转账（向其他用户转账）。
     *
     * @param userId  当前登录用户ID
     * @param request 转账请求（目标用户、金额等）
     * @return 更新后钱包视图的通用响应
     */
    @PostMapping("/transfer")
    public Result<WalletView> transfer(
            @RequestAttribute Integer userId,
            @RequestBody WalletTransferRequest request) {
        return Result.ok(walletService.transfer(userId, request));
    }
}
