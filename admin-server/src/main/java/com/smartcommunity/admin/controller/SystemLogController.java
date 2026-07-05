package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.SystemLog;
import com.smartcommunity.admin.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志控制器，提供操作日志的分页查询和筛选功能。
 * 请求路径前缀：/api/admin/system-log
 */
@RestController
@RequestMapping("/api/admin/system-log")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService systemLogService;

    /**
     * 分页查询系统操作日志，支持关键词和操作结果筛选。
     *
     * @param page    页码，默认 1
     * @param size    每页条数，默认 10
     * @param keyword 搜索关键词（操作人、接口地址等）
     * @param success 操作结果筛选（true=成功，false=失败）
     * @return 系统日志分页数据
     */
    @GetMapping("/page")
    public Result<Page<SystemLog>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean success) {
        return Result.ok(systemLogService.page(page, size, keyword, success));
    }
}
