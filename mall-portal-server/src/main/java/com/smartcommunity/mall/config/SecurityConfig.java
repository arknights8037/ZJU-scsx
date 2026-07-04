package com.smartcommunity.mall.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcommunity.mall.common.Result;
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

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 未登录 / Token 无效时返回 JSON（而非默认的空白 403 页面）
     */
    private void writeJsonError(HttpServletResponse response, int httpStatus, int bizCode, String message) {
        try {
            response.setStatus(httpStatus);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail(bizCode, message)));
        } catch (Exception ignored) {
            // 响应已提交，无法写入
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})  // 启用 CORS
            .csrf(csrf -> csrf.disable())
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
                // 公开接口
                .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                .requestMatchers("/api/goods/**", "/api/category/**", "/api/store/**", "/api/special/**").permitAll()
                // 图片由 img 标签直接访问，不会携带 Authorization 请求头，因此静态图片需要公开。
                .requestMatchers("/mall-uploads/**").permitAll()
                // 兼容老师示例工程中的旧路径
                .requestMatchers("/goods/**", "/store/**", "/carts/**", "/orders/**").permitAll()
                // 其余接口需要认证
                .anyRequest().authenticated()
            )
            // JWT 过滤器放在 Spring Security 的认证过滤器之前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
