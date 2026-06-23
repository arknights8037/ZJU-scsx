package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserMapper userMapper;
    private final RoleUserMapper roleUserMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<AdminUser> p = userMapper.selectPage(new Page<>(page, size), null);
        return Result.ok(Map.of("total", p.getTotal(), "records", p.getRecords()));
    }

    @PostMapping
    public Result<Void> save(@RequestBody AdminUser user) {
        if (user.getId() == null) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }
        userMapper.insertOrUpdate(user);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        AdminUser u = new AdminUser(); u.setId(id); u.setUserStatus(status);
        userMapper.updateById(u);
        return Result.ok();
    }

    @PostMapping("/{userId}/roles")
    public Result<Void> setRoles(@PathVariable Integer userId, @RequestBody List<Integer> roleIds) {
        roleUserMapper.delete(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId));
        for (Integer rid : roleIds) {
            RoleUser ru = new RoleUser(); ru.setUserId(userId); ru.setRoleId(rid);
            roleUserMapper.insert(ru);
        }
        return Result.ok();
    }

    @GetMapping("/{userId}/roles")
    public Result<List<Integer>> getRoles(@PathVariable Integer userId) {
        return Result.ok(roleUserMapper.selectList(
                new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId))
                .stream().map(RoleUser::getRoleId).collect(Collectors.toList()));
    }
}
