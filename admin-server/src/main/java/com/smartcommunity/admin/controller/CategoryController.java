package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Category;
import com.smartcommunity.admin.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * 商品类别管理控制器，提供类别列表查询、新增/编辑和删除功能。
 * 类别分为一级类别（categoryType=1）和二级类别（categoryType=2），二级类别需要关联父级。
 * 请求路径前缀：/api/admin/category
 */
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryMapper categoryMapper;

    /**
     * 获取所有类别列表，按 categoryType 和 id 升序排列。
     *
     * @return 类别列表响应
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryMapper.selectList(new LambdaQueryWrapper<Category>()
            .orderByAsc(Category::getCategoryType)
            .orderByAsc(Category::getId)));
    }

    /**
     * 新增或更新类别。
     * 一级类别自动设置 parentId=0；二级类别必须指定有效的父级类别。
     *
     * @param c 类别实体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Category c) {
        // 校验类别名称必填
        if (!StringUtils.hasText(c.getCategoryName())) {
            throw new RuntimeException("请填写类别名称");
        }
        // 未指定类别类型时默认为一级类别
        if (c.getCategoryType() == null) c.setCategoryType(1);
        if (c.getCategoryType() == 1) {
            // 一级类别的父 ID 固定为 0
            c.setParentId(0);
        } else if (c.getParentId() == null || categoryMapper.selectById(c.getParentId()) == null) {
            // 二级类别必须关联一个已有的一级类别
            throw new RuntimeException("请选择有效的父级类别");
        }
        categoryMapper.insertOrUpdate(c);
        return Result.ok();
    }

    /**
     * 根据 ID 删除类别。
     *
     * @param id 类别 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { categoryMapper.deleteById(id); return Result.ok(); }
}
