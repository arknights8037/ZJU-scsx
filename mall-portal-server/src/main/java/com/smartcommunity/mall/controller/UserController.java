package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Result<Map<String, String>> login(@RequestBody Map<String, String> params) {
        String token = userService.login(params.get("phone"), params.get("password"));
        return Result.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        return Result.ok(userService.register(user));
    }
}
