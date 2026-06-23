package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.GoodsCategory;
import com.smartcommunity.mall.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final GoodsService goodsService;

    @GetMapping("/all")
    public Result<List<GoodsCategory>> all() {
        return Result.ok(goodsService.getAllCategories());
    }

    @GetMapping("/children")
    public Result<List<GoodsCategory>> children(@RequestParam Integer parentId) {
        return Result.ok(goodsService.getSubCategories(parentId));
    }
}
