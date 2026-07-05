package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.GoodsCategory;
import com.smartcommunity.mall.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器，提供商品分类的列表和子分类查询功能。
 * 请求路径前缀：/api/category
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final GoodsService goodsService;

    /**
     * 获取所有商品分类列表。
     *
     * @return 包含全部分类列表的通用响应
     */
    @GetMapping("/all")
    public Result<List<GoodsCategory>> all() {
        return Result.ok(goodsService.getAllCategories());
    }

    /**
     * 根据父分类ID获取子分类列表。
     *
     * @param parentId 父分类ID
     * @return 子分类列表的通用响应
     */
    @GetMapping("/children")
    public Result<List<GoodsCategory>> children(@RequestParam Integer parentId) {
        return Result.ok(goodsService.getSubCategories(parentId));
    }
}
