package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.entity.SystemLog;
import com.smartcommunity.admin.mapper.SystemLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理端系统操作日志服务。
 * 提供操作日志的分页查询，支持按关键词和成功/失败状态筛选。
 */
@Service
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogMapper systemLogMapper;

    /**
     * 分页查询系统操作日志。
     * 支持按关键词（请求地址、操作模块、操作名称、IP、错误信息）和成功/失败状态筛选。
     *
     * @param page    页码
     * @param size    每页条数
     * @param keyword 搜索关键词
     * @param success 是否成功（null 表示全部）
     * @return 系统日志分页结果
     */
    public Page<SystemLog> page(int page, int size, String keyword, Boolean success) {
        // 日志表可能增长较快，分页参数统一收口，防止前端误传 size 造成大查询。
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, Math.min(size, 100));
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(success != null, SystemLog::getSuccess, success);
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(item -> item
                .like(SystemLog::getRequestUri, value)
                .or()
                .like(SystemLog::getOperationModule, value)
                .or()
                .like(SystemLog::getOperationName, value)
                .or()
                .like(SystemLog::getClientIp, value)
                .or()
                .like(SystemLog::getErrorMessage, value));
        }
        wrapper.orderByDesc(SystemLog::getCreateTime).orderByDesc(SystemLog::getId);
        return systemLogMapper.selectPage(new Page<>(safePage, safeSize), wrapper);
    }
}
