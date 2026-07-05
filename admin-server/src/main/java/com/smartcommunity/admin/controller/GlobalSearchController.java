package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.GlobalSearchItem;
import com.smartcommunity.admin.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 全局搜索控制器，提供跨模块的全局关键字搜索功能。
 * 请求路径前缀：/api/admin/global-search
 */
@RestController
@RequestMapping("/api/admin/global-search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    /**
     * 根据关键字进行全局搜索，结果涵盖订单、商品、用户等多个模块。
     *
     * @param keyword 搜索关键字
     * @return 搜索结果条目列表
     */
    @GetMapping
    public Result<List<GlobalSearchItem>> search(@RequestParam String keyword) {
        return Result.ok(globalSearchService.search(keyword));
    }
}
