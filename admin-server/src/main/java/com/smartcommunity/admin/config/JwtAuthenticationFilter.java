package com.smartcommunity.admin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器 —— 从 Authorization 头提取 Token 并设置安全上下文。
 * <p>
 * 继承 OncePerRequestFilter 确保每次请求只执行一次过滤逻辑。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /**
     * 核心过滤方法：从请求中提取 Token，解析用户 ID 并注入 Spring Security 上下文。
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                // 从 Token 中解析出用户 ID
                Integer userId = jwtUtils.getUserId(token);
                // 构造认证令牌并注入 Spring Security 上下文
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 将 userId 存入请求属性，供后续 Controller/Interceptor 使用
                request.setAttribute("userId", userId);
            } catch (Exception e) {
                // Token 无效时清除安全上下文
                SecurityContextHolder.clearContext();
            }
        }

        // 继续执行后续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 Bearer Token。
     *
     * @param request HTTP 请求
     * @return Token 字符串，如果不存在或格式不对则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 去掉 "Bearer " 前缀，取实际的 Token 值
            return bearerToken.substring(7);
        }
        return null;
    }
}
