package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.config.JwtUtils;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理端认证授权服务。
 * 处理管理员登录、密码验证、JWT 签发、角色与菜单权限查询。
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminUserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleUserMapper roleUserMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 管理员登录认证。
     *
     * @param phone    管理员手机号
     * @param password 明文密码
     * @return JWT token 字符串
     */
    public String login(String phone, String password) {
        LambdaQueryWrapper<AdminUser> w = new LambdaQueryWrapper<>();
        w.eq(AdminUser::getPhone, phone);
        AdminUser user = userMapper.selectOne(w);
        if (user == null) throw new RuntimeException("用户不存在");
        // 校验密码：数据库存的是 BCrypt 加密后的密文
        if (!passwordEncoder.matches(password, user.getUserPassword()))
            throw new RuntimeException("密码错误");
        // 校验账号状态：status != 1 表示被禁用
        if (user.getUserStatus() != 1) throw new RuntimeException("账号已被禁用");
        return jwtUtils.generateToken(user.getId(), phone);
    }

    /**
     * 根据用户 ID 获取管理员信息。
     *
     * @param id 用户 ID
     * @return AdminUser 对象
     */
    public AdminUser getUserById(Integer id) { return userMapper.selectById(id); }

    /**
     * 根据用户 ID 获取该用户的所有角色。
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    public List<Role> getUserRoles(Integer userId) {
        LambdaQueryWrapper<RoleUser> w = new LambdaQueryWrapper<>();
        w.eq(RoleUser::getUserId, userId);
        List<Integer> roleIds = roleUserMapper.selectList(w).stream()
                .map(RoleUser::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return List.of();
        return roleMapper.selectBatchIds(roleIds);
    }

    /**
     * 根据用户 ID 获取该用户有权访问的菜单（仅已启用的菜单）。
     *
     * @param userId 用户 ID
     * @return 菜单列表（扁平结构，需调用 buildMenuTree 转换为树形）
     */
    public List<Menu> getUserMenus(Integer userId) {
        // 先查用户角色，再通过角色-菜单关联表收集所有菜单 ID
        List<Role> roles = getUserRoles(userId);
        Set<Integer> menuIds = new HashSet<>();
        for (Role role : roles) {
            LambdaQueryWrapper<RoleMenu> w = new LambdaQueryWrapper<>();
            w.eq(RoleMenu::getRoleId, role.getId());
            roleMenuMapper.selectList(w).forEach(rm -> menuIds.add(rm.getMenuId()));
        }
        if (menuIds.isEmpty()) return List.of();
        // 只查询菜单状态为启用（1）的菜单，并按 parentId、id 排序以便构建树
        LambdaQueryWrapper<Menu> mw = new LambdaQueryWrapper<>();
        mw.in(Menu::getId, menuIds).eq(Menu::getMenuState, 1).orderByAsc(Menu::getParentId, Menu::getId);
        return menuMapper.selectList(mw);
    }

    /**
     * 将扁平的菜单列表构建为树形结构。
     *
     * @param menus 扁平菜单列表
     * @return 树形菜单列表
     */
    /**
     * 将扁平的菜单列表构建为树形结构。
     *
     * @param menus 扁平菜单列表
     * @return 树形菜单列表
     */
    public List<Menu> buildMenuTree(List<Menu> menus) {
        // 先按 parentId 分组，过滤掉根节点（parentId == 0 或 null）
        Map<Integer, List<Menu>> childrenMap = menus.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() != 0)
                .collect(Collectors.groupingBy(Menu::getParentId));
        List<Menu> roots = menus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .collect(Collectors.toList());
        for (Menu root : roots) {
            setChildren(root, childrenMap);
        }
        return roots;
    }

    /**
     * 递归设置菜单的子节点。
     *
     * @param parent      父菜单
     * @param childrenMap 按 parentId 分组的子菜单映射
     */
    private void setChildren(Menu parent, Map<Integer, List<Menu>> childrenMap) {
        List<Menu> children = childrenMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            // 递归为每个子节点设置其下级子节点
            for (Menu child : children) setChildren(child, childrenMap);
        }
    }
}
