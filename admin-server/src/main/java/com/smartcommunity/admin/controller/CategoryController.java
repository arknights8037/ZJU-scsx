package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Category;
import com.smartcommunity.admin.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<Category>> list() { return Result.ok(categoryMapper.selectList(null)); }

    @PostMapping
    public Result<Void> save(@RequestBody Category c) { categoryMapper.insertOrUpdate(c); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { categoryMapper.deleteById(id); return Result.ok(); }
}
