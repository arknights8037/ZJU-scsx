package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.PersonalExpenseForm;
import com.smartcommunity.mall.dto.PersonalExpenseSummary;
import com.smartcommunity.mall.entity.PersonalExpense;
import com.smartcommunity.mall.service.PersonalExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 个人记账控制器，提供收支记录查询、汇总统计、新增和删除功能。
 * 请求路径前缀：/api/personal-expense
 */
@RestController
@RequestMapping("/api/personal-expense")
@RequiredArgsConstructor
public class PersonalExpenseController {
    private final PersonalExpenseService expenseService;

    /**
     * 分页查询当前用户的个人收支记录。
     *
     * @param userId 当前登录用户ID
     * @param page   页码，默认第1页
     * @param size   每页条数，默认10条
     * @return 个人收支记录分页的通用响应
     */
    @GetMapping("/page")
    public Result<Page<PersonalExpense>> page(@RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(expenseService.page(userId, page, size));
    }

    /**
     * 获取当前用户的收支汇总统计（总收入、总支出等）。
     *
     * @param userId 当前登录用户ID
     * @return 收支汇总的通用响应
     */
    @GetMapping("/summary")
    public Result<PersonalExpenseSummary> summary(@RequestAttribute Integer userId) {
        return Result.ok(expenseService.summary(userId));
    }

    /**
     * 手动新增一条个人收支记录。
     *
     * @param userId 当前登录用户ID
     * @param form   收支记录表单（类型、金额、备注等）
     * @return 新增后的收支记录的通用响应
     */
    @PostMapping
    public Result<PersonalExpense> create(@RequestAttribute Integer userId,
            @RequestBody PersonalExpenseForm form) {
        return Result.ok(expenseService.createManual(userId, form));
    }

    /**
     * 删除指定的个人收支记录。
     *
     * @param userId 当前登录用户ID
     * @param id     收支记录ID
     * @return 空成功的通用响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute Integer userId, @PathVariable Long id) {
        expenseService.deleteManual(userId, id);
        return Result.ok();
    }
}
