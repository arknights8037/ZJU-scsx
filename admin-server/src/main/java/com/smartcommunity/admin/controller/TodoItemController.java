package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.BusinessTodoOverview;
import com.smartcommunity.admin.service.BusinessTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 待办事项控制器，提供后台待办事项概览数据。
 * 请求路径前缀：/api/admin/todo
 */
@RestController
@RequestMapping("/api/admin/todo")
@RequiredArgsConstructor
public class TodoItemController {

    private final BusinessTodoService businessTodoService;

    /**
     * 获取待办事项概览，包括各分类的待办数量和状态统计。
     *
     * @return 待办事项概览数据
     */
    @GetMapping("/list")
    public Result<BusinessTodoOverview> list() {
        return Result.ok(businessTodoService.overview());
    }
}
