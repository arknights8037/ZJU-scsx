package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制器，提供角色的增删查以及角色-菜单权限分配功能。
 * 请求路径前缀：/api/admin/role
 */
@RestController
@RequestMapping("/api/admin/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleUserMapper roleUserMapper;

    /**
     * 获取所有角色列表。
     *
     * @return 角色列表响应
     */
    @GetMapping("/list")
    public Result<List<Role>> list() { return Result.ok(roleMapper.selectList(null)); }

    /**
     * 新增或更新角色。
     *
     * @param role 角色实体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Role role) { roleMapper.insertOrUpdate(role); return Result.ok(); }

    /**
     * 根据 ID 删除角色。
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { roleMapper.deleteById(id); return Result.ok(); }

    /**
     * 为指定角色分配菜单权限。
     * 先清空该角色原有的菜单关联，再批量插入新的关联记录。
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 列表
     * @return 操作结果
     */
    @PostMapping("/{roleId}/menus")
    public Result<Void> setMenus(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds) {
        // 先删除该角色原有的所有菜单关联
        LambdaQueryWrapper<RoleMenu> w = new LambdaQueryWrapper<>();
        w.eq(RoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(w);
        // 重新插入新的菜单关联记录
        for (Integer menuId : menuIds) {
            RoleMenu rm = new RoleMenu(); rm.setRoleId(roleId); rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
        return Result.ok();
    }

    /**
     * 获取指定角色已分配的菜单 ID 列表。
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    @GetMapping("/{roleId}/menus")
    public Result<List<Integer>> getMenus(@PathVariable Integer roleId) {
        return Result.ok(roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId))
                .stream().map(RoleMenu::getMenuId).collect(Collectors.toList()));
    }
}
