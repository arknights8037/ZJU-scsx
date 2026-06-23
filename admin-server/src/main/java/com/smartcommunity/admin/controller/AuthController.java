package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.AdminUser;
import com.smartcommunity.admin.entity.Menu;
import com.smartcommunity.admin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String token = authService.login(params.get("phone"), params.get("password"));
        return Result.ok(Map.of("token", token));
    }

    @GetMapping("/user-info")
    public Result<Map<String, Object>> userInfo(@RequestAttribute Integer userId) {
        AdminUser user = authService.getUserById(userId);
        List<Menu> menus = authService.getUserMenus(userId);
        return Result.ok(Map.of("user", user, "menus", authService.buildMenuTree(menus)));
    }
}
