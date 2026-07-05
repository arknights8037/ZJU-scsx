package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.PasswordChangeForm;
import com.smartcommunity.mall.dto.AvatarUpdateForm;
import com.smartcommunity.mall.dto.UserProfileForm;
import com.smartcommunity.mall.dto.UserProfileView;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器，提供用户登录、注册、资料查询/修改、头像更新、密码修改功能。
 * 请求路径前缀：/api/user
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录接口，验证手机号和密码，返回Token和用户信息。
     *
     * @param params 登录参数，包含 phone（手机号）和 password（密码）
     * @return 包含 token、userId、phone、userName、avatar 的通用响应
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        // 调用登录服务，验证密码并生成JWT令牌
        String token = userService.login(phone, params.get("password"));
        // 获取用户信息用于返回
        User user = userService.getByPhone(phone);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("phone", user.getPhone());
        data.put("userName", user.getUserName());
        data.put("avatar", user.getAvatar());
        return Result.ok(data);
    }

    /**
     * 用户注册接口，创建新用户。
     *
     * @param user 注册信息（手机号、密码、用户名等）
     * @return 注册成功的用户信息的通用响应
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        return Result.ok(userService.register(user));
    }

    /**
     * 获取当前登录用户资料。userId 由 JWT 过滤器写入 request。
     */
    @GetMapping("/profile")
    public Result<UserProfileView> profile(@RequestAttribute Integer userId) {
        return Result.ok(userService.getProfile(userId));
    }

    /**
     * 修改当前登录用户资料。
     */
    @PutMapping("/profile")
    public Result<UserProfileView> updateProfile(
            @RequestAttribute Integer userId,
            @RequestBody UserProfileForm form) {
        return Result.ok(userService.updateProfile(userId, form));
    }

    /**
     * 更新当前登录用户的头像。
     *
     * @param userId 当前登录用户ID
     * @param form   头像更新表单（包含头像URL）
     * @return 新头像URL的通用响应
     */
    @PutMapping("/avatar")
    public Result<String> updateAvatar(
            @RequestAttribute Integer userId,
            @RequestBody AvatarUpdateForm form) {
        // 如果表单为空则传null，由Service处理
        return Result.ok(userService.updateAvatar(userId, form == null ? null : form.getAvatar()));
    }

    /**
     * 修改当前登录用户密码。
     */
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestAttribute Integer userId,
            @RequestBody PasswordChangeForm form) {
        userService.changePassword(userId, form);
        return Result.ok();
    }
}
