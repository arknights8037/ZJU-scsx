package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Menu;
import com.smartcommunity.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuMapper menuMapper;

    @GetMapping("/list")
    public Result<List<Menu>> list() { return Result.ok(menuMapper.selectList(null)); }

    @PostMapping
    public Result<Void> save(@RequestBody Menu menu) { menuMapper.insertOrUpdate(menu); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { menuMapper.deleteById(id); return Result.ok(); }
}
