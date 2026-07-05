package com.smartcommunity.admin.config;

import com.smartcommunity.admin.entity.SystemLog;
import com.smartcommunity.admin.mapper.SystemLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * 系统操作日志拦截器。
 * <p>
 * 在请求处理前后记录操作日志，包括请求路径、参数、耗时、操作人、结果状态等信息。
 * 日志写入失败不影响正常业务请求。
 */
@Component
@RequiredArgsConstructor
public class SystemLogInterceptor implements HandlerInterceptor {

    /** 请求开始时间在 request attribute 中存储的 Key */
    private static final String START_TIME = "systemLogStartTime";
    private final SystemLogMapper systemLogMapper;

    /**
     * 预处理：在请求处理前记录开始时间。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return 始终返回 true，不拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    /**
     * 后处理：请求完成后构造并保存系统日志。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @param ex       请求处理过程中抛出的异常（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 跳过 OPTIONS 预检请求，不记录日志
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return;
        }
        try {
            // 认证过滤器会把 userId 放进 request attribute；未登录公开接口则允许为空
            SystemLog log = new SystemLog();
            Object userId = request.getAttribute("userId");
            if (userId instanceof Integer id) {
                log.setUserId(id);
            }
            log.setRequestMethod(request.getMethod());
            log.setRequestUri(limit(request.getRequestURI(), 300));
            log.setQueryString(limit(request.getQueryString(), 500));
            log.setOperationModule(resolveModule(request.getRequestURI()));
            log.setOperationName(resolveOperation(request.getMethod(), request.getRequestURI()));
            log.setClientIp(resolveClientIp(request));
            log.setStatusCode(response.getStatus());
            log.setSuccess(ex == null && response.getStatus() < 400);
            log.setErrorMessage(ex == null ? null : limit(ex.getMessage(), 500));
            Object startTime = request.getAttribute(START_TIME);
            long start = startTime instanceof Long value ? value : System.currentTimeMillis();
            log.setCostMs(Math.max(0, System.currentTimeMillis() - start));
            log.setCreateTime(LocalDateTime.now());
            systemLogMapper.insert(log);
        } catch (Exception ignored) {
            // 日志写入不能影响正常业务请求
        }
    }

    /**
     * 根据请求 URI 解析操作模块名称。
     *
     * @param uri 请求 URI
     * @return 模块名称
     */
    private String resolveModule(String uri) {
        // 按接口前缀粗粒度归类，前端日志页可以直接按"业务模块"筛选
        if (uri == null) return "系统";
        if (uri.startsWith("/api/admin/order")) return "后台订单";
        if (uri.startsWith("/api/admin/community")) return "后台社区";
        if (uri.startsWith("/api/admin/goods")) return "后台商品";
        if (uri.startsWith("/api/admin/system-log")) return "系统日志";
        if (uri.startsWith("/api/admin/global-search")) return "后台全局搜索";
        if (uri.startsWith("/api/admin/todo")) return "社区业务待办";
        if (uri.startsWith("/api/order")) return "居民订单";
        if (uri.startsWith("/api/wallet")) return "居民钱包";
        if (uri.startsWith("/api/personal-expense")) return "个人消费账单";
        if (uri.startsWith("/api/favorite")) return "居民收藏";
        if (uri.startsWith("/api/cart")) return "居民购物车";
        if (uri.startsWith("/api/community")) return "居民社区";
        if (uri.startsWith("/api/goods")) return "商品浏览";
        if (uri.startsWith("/api/auth") || uri.startsWith("/api/user")) return "登录用户";
        return "系统接口";
    }

    /**
     * 根据 HTTP 方法和 URI 解析操作名称。
     *
     * @param method HTTP 方法（GET/POST/PUT/DELETE 等）
     * @param uri    请求 URI
     * @return 操作名称
     */
    private String resolveOperation(String method, String uri) {
        String action = switch (method.toUpperCase(Locale.ROOT)) {
            case "GET" -> "查询";
            case "POST" -> "新增/提交";
            case "PUT", "PATCH" -> "修改";
            case "DELETE" -> "删除";
            default -> method;
        };
        return action + " " + limit(uri, 160);
    }

    /**
     * 解析客户端真实 IP。
     * <p>
     * 兼容本地直连和 Nginx/网关转发两种部署方式。
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String resolveClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            // X-Forwarded-For 可能包含逗号分隔的多个 IP，取第一个
            return header.split(",")[0].trim();
        }
        header = request.getHeader("X-Real-IP");
        return header == null || header.isBlank() ? request.getRemoteAddr() : header;
    }

    /**
     * 截断字符串到指定最大长度。
     *
     * @param value 原始字符串
     * @param max   最大长度
     * @return 截断后的字符串
     */
    private String limit(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }
}
