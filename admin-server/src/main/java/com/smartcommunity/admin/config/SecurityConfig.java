package com.smartcommunity.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcommunity.admin.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring Security 安全配置类。
 * <p>
 * 采用无状态（STATELESS）JWT 认证方案，配置 CORS、CSRF、接口权限规则，
 * 并在 Spring Security 过滤器链中注入自定义的 JWT 认证过滤器。
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 自定义 JWT 过滤器。
     *
     * 过滤器会从 Authorization: Bearer xxx 中解析用户身份，并把 userId 等信息写入请求上下文。
     * 后续 Controller 只需要通过 @RequestAttribute 取值，不再相信前端传来的 userId。
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器 Bean。
     * <p>
     * BCrypt 每次加密都会带随机盐，比明文或固定 MD5 更适合保存登录密码。
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 跨域（CORS）过滤器 Bean。
     * <p>
     * 前后端分离开发时，浏览器会先发 OPTIONS 预检请求。
     * 这里统一放开跨域，真正的权限控制仍然由 JWT + Spring Security 完成。
     *
     * @return CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // 允许所有来源
        config.setAllowedHeaders(List.of("*"));         // 允许所有请求头
        config.setAllowedMethods(List.of("*"));         // 允许所有 HTTP 方法
        config.setAllowCredentials(true);               // 允许携带凭证（Cookie/Authorization）
        config.setMaxAge(3600L);                        // 预检请求缓存 1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 未登录 / Token 无效时返回 JSON 格式的错误信息。
     *
     * @param response   HTTP 响应
     * @param httpStatus HTTP 状态码
     * @param bizCode    业务状态码
     * @param message    错误消息
     */
    private void writeJsonError(HttpServletResponse response, int httpStatus, int bizCode, String message) {
        try {
            response.setStatus(httpStatus);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail(bizCode, message)));
        } catch (Exception ignored) {
        }
    }

    /**
     * 核心安全过滤器链配置。
     * <p>
     * 配置 CORS、CSRF（关闭）、无状态会话、接口鉴权规则以及 JWT 过滤器注册。
     *
     * @param http HttpSecurity 构建器
     * @return 构建好的 SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})  // 启用 CORS
            // 前端使用 JWT，不依赖后端 Session；关闭 CSRF 可以避免无状态接口被误拦截。
            .csrf(csrf -> csrf.disable())
            // STATELESS 表示服务端不保存登录会话，每个请求都通过 Token 独立认证。
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 未授权返回 JSON
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                    writeJsonError(res, 401, 401, "请先登录"))
                .accessDeniedHandler((req, res, e) ->
                    writeJsonError(res, 403, 401, "暂无权限"))
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 管理端公开接口
                .requestMatchers("/api/auth/login").permitAll()
                // 居民端公开接口
                .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                // 商品浏览、分类、门店、促销属于游客也可以访问的商城信息
                .requestMatchers("/api/goods/**", "/api/category/**", "/api/store/**", "/api/special/**").permitAll()
                // 兼容老师示例工程中的旧路径
                .requestMatchers("/goods/**", "/store/**", "/carts/**", "/orders/**").permitAll()
                // 图片由后端统一提供，img 标签访问静态资源时不会携带 Token
                .requestMatchers("/admin-uploads/**", "/mall-uploads/**", "/community-products/**").permitAll()
                // 其余接口需要认证
                .anyRequest().authenticated()
            )
            // JWT 过滤器放在 Spring Security 的认证过滤器之前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
