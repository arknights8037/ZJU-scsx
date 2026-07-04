package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Category;
import com.smartcommunity.admin.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryMapper.selectList(new LambdaQueryWrapper<Category>()
            .orderByAsc(Category::getCategoryType)
            .orderByAsc(Category::getId)));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Category c) {
        if (!StringUtils.hasText(c.getCategoryName())) {
            throw new RuntimeException("请填写类别名称");
        }
        if (c.getCategoryType() == null) c.setCategoryType(1);
        if (c.getCategoryType() == 1) {
            c.setParentId(0);
        } else if (c.getParentId() == null || categoryMapper.selectById(c.getParentId()) == null) {
            throw new RuntimeException("请选择有效的父级类别");
        }
        categoryMapper.insertOrUpdate(c);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { categoryMapper.deleteById(id); return Result.ok(); }
}
