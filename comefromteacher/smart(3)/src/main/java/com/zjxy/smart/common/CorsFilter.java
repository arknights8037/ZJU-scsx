package com.zjxy.smart.common;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 这是一个跨域过滤器，用于处理跨域资源共享(CORS)问题
 * 通过设置HTTP响应头，允许前端应用访问后端API
 *
 * @Configuration 表示这是一个配置类
 * @WebFilter(filterName = "CorsFilter") 指定这是一个过滤器，名称为"CorsFilter"
 */
@WebFilter(filterName = "CorsFilter ")
@Configuration
class CorsFilter implements Filter {
    /**
     * 执行过滤操作的核心方法
     * @param req Servlet请求对象
     * @param res Servlet响应对象
     * @param chain 过滤器链，用于将请求传递给下一个过滤器
     * @throws IOException 可能发生的IO异常
     * @throws ServletException 可能发生的Servlet异常
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException, ServletException {
        // 将ServletResponse强制转换为HttpServletResponse，以便设置HTTP响应头
        HttpServletResponse response = (HttpServletResponse) res;
        // 设置允许所有来源的跨域请求
        response.setHeader("Access-Control-Allow-Origin","*");
        // 允许发送凭证信息（如cookies）
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 允许的HTTP方法
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
        // 预检请求的缓存时间（秒）
        response.setHeader("Access-Control-Max-Age", "3600");
        // 允许所有的请求头
        response.setHeader("Access-Control-Allow-Headers", "*");
        // 将请求传递给下一个过滤器
        chain.doFilter(req, res);
    }

}
