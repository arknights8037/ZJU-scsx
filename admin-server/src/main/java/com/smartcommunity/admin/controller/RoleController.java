package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleUserMapper roleUserMapper;

    @GetMapping("/list")
    public Result<List<Role>> list() { return Result.ok(roleMapper.selectList(null)); }

    @PostMapping
    public Result<Void> save(@RequestBody Role role) { roleMapper.insertOrUpdate(role); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { roleMapper.deleteById(id); return Result.ok(); }

    @PostMapping("/{roleId}/menus")
    public Result<Void> setMenus(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds) {
        LambdaQueryWrapper<RoleMenu> w = new LambdaQueryWrapper<>();
        w.eq(RoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(w);
        for (Integer menuId : menuIds) {
            RoleMenu rm = new RoleMenu(); rm.setRoleId(roleId); rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
        return Result.ok();
    }

    @GetMapping("/{roleId}/menus")
    public Result<List<Integer>> getMenus(@PathVariable Integer roleId) {
        return Result.ok(roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId))
                .stream().map(RoleMenu::getMenuId).collect(Collectors.toList()));
    }
}
