package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Menu;
import com.smartcommunity.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器，提供菜单的增删查操作。
 * 请求路径前缀：/api/admin/menu
 */
@RestController
@RequestMapping("/api/admin/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuMapper menuMapper;

    /**
     * 获取所有菜单列表。
     *
     * @return 菜单列表响应
     */
    @GetMapping("/list")
    public Result<List<Menu>> list() { return Result.ok(menuMapper.selectList(null)); }

    /**
     * 新增或更新菜单。
     *
     * @param menu 菜单实体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Menu menu) { menuMapper.insertOrUpdate(menu); return Result.ok(); }

    /**
     * 根据 ID 删除菜单。
     *
     * @param id 菜单 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { menuMapper.deleteById(id); return Result.ok(); }
}
