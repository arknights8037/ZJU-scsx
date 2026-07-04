package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.PasswordChangeForm;
import com.smartcommunity.mall.dto.UserProfileForm;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String token = userService.login(phone, params.get("password"));
        User user = userService.getByPhone(phone);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("phone", user.getPhone());
        data.put("userName", user.getUserName());
        data.put("avatar", user.getAvatar());
        return Result.ok(data);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        return Result.ok(userService.register(user));
    }

    /**
     * 获取当前登录用户资料。userId 由 JWT 过滤器写入 request。
     */
    @GetMapping("/profile")
    public Result<User> profile(@RequestAttribute Integer userId) {
        return Result.ok(userService.getById(userId));
    }

    /**
     * 修改当前登录用户资料。
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(
            @RequestAttribute Integer userId,
            @RequestBody UserProfileForm form) {
        return Result.ok(userService.updateProfile(userId, form));
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
