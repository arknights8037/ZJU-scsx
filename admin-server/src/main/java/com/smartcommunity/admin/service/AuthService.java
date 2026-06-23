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

    public String login(String phone, String password) {
        LambdaQueryWrapper<AdminUser> w = new LambdaQueryWrapper<>();
        w.eq(AdminUser::getPhone, phone);
        AdminUser user = userMapper.selectOne(w);
        if (user == null) throw new RuntimeException("用户不存在");
        if (!passwordEncoder.matches(password, user.getUserPassword()))
            throw new RuntimeException("密码错误");
        if (user.getUserStatus() != 1) throw new RuntimeException("账号已被禁用");
        return jwtUtils.generateToken(user.getId(), phone);
    }

    public AdminUser getUserById(Integer id) { return userMapper.selectById(id); }

    public List<Role> getUserRoles(Integer userId) {
        LambdaQueryWrapper<RoleUser> w = new LambdaQueryWrapper<>();
        w.eq(RoleUser::getUserId, userId);
        List<Integer> roleIds = roleUserMapper.selectList(w).stream()
                .map(RoleUser::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return List.of();
        return roleMapper.selectBatchIds(roleIds);
    }

    public List<Menu> getUserMenus(Integer userId) {
        List<Role> roles = getUserRoles(userId);
        Set<Integer> menuIds = new HashSet<>();
        for (Role role : roles) {
            LambdaQueryWrapper<RoleMenu> w = new LambdaQueryWrapper<>();
            w.eq(RoleMenu::getRoleId, role.getId());
            roleMenuMapper.selectList(w).forEach(rm -> menuIds.add(rm.getMenuId()));
        }
        if (menuIds.isEmpty()) return List.of();
        LambdaQueryWrapper<Menu> mw = new LambdaQueryWrapper<>();
        mw.in(Menu::getId, menuIds).eq(Menu::getMenuState, 1).orderByAsc(Menu::getParentId, Menu::getId);
        return menuMapper.selectList(mw);
    }

    public List<Menu> buildMenuTree(List<Menu> menus) {
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

    private void setChildren(Menu parent, Map<Integer, List<Menu>> childrenMap) {
        List<Menu> children = childrenMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (Menu child : children) setChildren(child, childrenMap);
        }
    }
}
