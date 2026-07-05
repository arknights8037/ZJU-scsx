package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.AdminUser;
import com.smartcommunity.admin.entity.Menu;
import com.smartcommunity.admin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证控制器，处理管理端用户登录、获取用户信息及菜单权限的接口。
 * 请求路径前缀：/api/auth
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 用户登录接口。
     *
     * @param params 请求体，包含 phone（手机号）和 password（密码）
     * @return 包含 JWT token 的响应结果
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        // 调用认证服务进行登录验证，成功后返回 token
        String token = authService.login(params.get("phone"), params.get("password"));
        return Result.ok(Map.of("token", token));
    }

    /**
     * 获取当前登录用户的信息及其可访问的菜单树。
     *
     * @param userId 从请求属性中注入的用户 ID（在 JWT 过滤器中设置）
     * @return 包含用户基本信息和菜单树的响应结果
     */
    @GetMapping("/user-info")
    public Result<Map<String, Object>> userInfo(@RequestAttribute Integer userId) {
        AdminUser user = authService.getUserById(userId);
        List<Menu> menus = authService.getUserMenus(userId);
        // 将菜单列表转为树形结构返回给前端动态渲染侧边栏
        return Result.ok(Map.of("user", user, "menus", authService.buildMenuTree(menus)));
    }
}
