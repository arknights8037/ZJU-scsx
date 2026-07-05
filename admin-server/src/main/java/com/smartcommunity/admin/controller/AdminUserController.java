package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import com.smartcommunity.mall.entity.UserResidentProfile;
import com.smartcommunity.mall.mapper.UserResidentProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理端用户管理控制器，提供管理员/居民账号的分页查询、新增/编辑、状态修改、角色分配等功能。
 * 管理员与居民账号共用同一张 user 表，通过 user_type 区分。
 * 请求路径前缀：/api/admin/user
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    // 管理端用户 mapper 操作的也是 user 表；本项目把管理员和居民账号放在同一张表中。
    private final AdminUserMapper userMapper;
    // 用户-角色关系表，用于后台权限分配。
    private final RoleUserMapper roleUserMapper;
    // 居民住户扩展资料，后台用户管理页需要展示业主楼栋、单元、门牌号等信息。
    private final UserResidentProfileMapper residentProfileMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表，同时关联查询用户的居民扩展资料（楼栋、门牌号等）。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10
     * @return 包含 total 和 records 的 Map，records 中合并了 user 表和 resident_profile 表的数据
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<AdminUser> p = userMapper.selectPage(new Page<>(page, size), null);
        // 先取出本页所有 userId，再一次性查询扩展资料，避免循环中每行查一次数据库。
        Set<Integer> userIds = p.getRecords().stream().map(AdminUser::getId).collect(Collectors.toSet());
        Map<Integer, UserResidentProfile> profileMap = userIds.isEmpty() ? Collections.emptyMap()
            : residentProfileMapper.selectList(new LambdaQueryWrapper<UserResidentProfile>()
                .in(UserResidentProfile::getUserId, userIds))
                .stream()
                .collect(Collectors.toMap(UserResidentProfile::getUserId, Function.identity(), (a, b) -> a));
        List<Map<String, Object>> records = p.getRecords().stream()
            .map(user -> toUserMap(user, profileMap.get(user.getId())))
            .toList();
        return Result.ok(Map.of("total", p.getTotal(), "records", records));
    }

    /**
     * 新增或编辑用户。
     * 接收 Map 对象以同时处理 user 表字段和 resident_profile 扩展字段，保存时会自动拆分为两条记录。
     *
     * @param body 包含用户信息和住户扩展信息的 Map
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Map<String, Object> body) {
        /*
         * 这里接收 Map 而不是直接接 AdminUser：
         * 1. 前端同一个编辑弹窗会提交账号字段 + 住户字段；
         * 2. 住户字段不属于 AdminUser 实体，需要手动拆出来保存到 user_resident_profile；
         * 3. 这样可以保持原始 user 表结构不被社区扩展字段污染。
         */
        AdminUser user = new AdminUser();
        user.setId(toInteger(body.get("id")));
        user.setPhone(readString(body, "phone"));
        user.setUserName(readString(body, "userName"));
        user.setSex(readString(body, "sex"));
        user.setMail(readString(body, "mail"));
        user.setAvatar(readString(body, "avatar"));
        user.setUserStatus(toInteger(body.get("userStatus")));
        if (user.getUserStatus() == null) {
            // 新增用户默认启用，避免创建后因为状态为空导致无法登录或展示异常。
            user.setUserStatus(1);
        }
        if (user.getId() == null) {
            // 后台新增用户时如果没有填写密码，给一个默认密码，方便演示环境快速建号。
            String password = readString(body, "userPassword");
            user.setUserPassword(passwordEncoder.encode(StringUtils.hasText(password) ? password : "123456"));
        }
        userMapper.insertOrUpdate(user);

        UserResidentProfile profile = findResidentProfile(user.getId());
        if (profile == null) {
            profile = new UserResidentProfile();
            profile.setUserId(user.getId());
        }
        profile.setOwnerName(readString(body, "ownerName"));
        profile.setBuildingNo(readString(body, "buildingNo"));
        profile.setUnitNo(readString(body, "unitNo"));
        profile.setRoomNo(readString(body, "roomNo"));
        profile.setFullAddress(readString(body, "fullAddress"));
        profile.setEmergencyContact(readString(body, "emergencyContact"));
        profile.setEmergencyPhone(readString(body, "emergencyPhone"));
        residentProfileMapper.insertOrUpdate(profile);
        return Result.ok();
    }

    /**
     * 更新用户的启用/禁用状态。
     *
     * @param id     用户 ID
     * @param status 状态值（1=启用，0=禁用）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        AdminUser u = new AdminUser(); u.setId(id); u.setUserStatus(status);
        userMapper.updateById(u);
        return Result.ok();
    }

    /**
     * 为指定用户分配角色。
     * 先清空该用户原有的角色关联，再批量插入新的关联。
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 列表
     * @return 操作结果
     */
    @PostMapping("/{userId}/roles")
    public Result<Void> setRoles(@PathVariable Integer userId, @RequestBody List<Integer> roleIds) {
        roleUserMapper.delete(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId));
        for (Integer rid : roleIds) {
            RoleUser ru = new RoleUser(); ru.setUserId(userId); ru.setRoleId(rid);
            roleUserMapper.insert(ru);
        }
        return Result.ok();
    }

    /**
     * 获取指定用户已分配的角色 ID 列表。
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    @GetMapping("/{userId}/roles")
    public Result<List<Integer>> getRoles(@PathVariable Integer userId) {
        return Result.ok(roleUserMapper.selectList(
                new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId))
                .stream().map(RoleUser::getRoleId).collect(Collectors.toList()));
    }

    private Map<String, Object> toUserMap(AdminUser user, UserResidentProfile profile) {
        // 用 LinkedHashMap 保持 JSON 字段顺序相对稳定，调试接口时更容易阅读。
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("id", user.getId());
        record.put("phone", user.getPhone());
        record.put("userName", user.getUserName());
        record.put("sex", user.getSex());
        record.put("mail", user.getMail());
        record.put("avatar", user.getAvatar());
        record.put("lastLoginTime", user.getLastLoginTime());
        record.put("userStatus", user.getUserStatus());
        record.put("createTime", user.getCreateTime());
        record.put("updateTime", user.getUpdateTime());
        record.put("ownerName", profile == null ? null : profile.getOwnerName());
        record.put("buildingNo", profile == null ? null : profile.getBuildingNo());
        record.put("unitNo", profile == null ? null : profile.getUnitNo());
        record.put("roomNo", profile == null ? null : profile.getRoomNo());
        record.put("fullAddress", profile == null ? null : profile.getFullAddress());
        record.put("emergencyContact", profile == null ? null : profile.getEmergencyContact());
        record.put("emergencyPhone", profile == null ? null : profile.getEmergencyPhone());
        return record;
    }

    private UserResidentProfile findResidentProfile(Integer userId) {
        // 扩展资料不存在时返回 null，保存逻辑会自动创建。
        return residentProfileMapper.selectOne(new LambdaQueryWrapper<UserResidentProfile>()
            .eq(UserResidentProfile::getUserId, userId)
            .last("limit 1"));
    }

    private String readString(Map<String, Object> body, String key) {
        // 统一处理前端传来的空字符串，减少 Service/Mapper 层到处判断。
        Object value = body.get(key);
        if (value == null) return null;
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private Integer toInteger(Object value) {
        // JSON 反序列化后数字可能是 Integer，也可能是字符串；这里统一转成 Integer。
        if (value == null || String.valueOf(value).isBlank()) return null;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(String.valueOf(value));
    }
}
