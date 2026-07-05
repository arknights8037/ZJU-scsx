package com.smartcommunity.admin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 系统日志拦截器注册配置类。
 * <p>
 * 注册 SystemLogInterceptor 拦截器，使其对 /api/** 路径生效，
 * 同时排除静态资源路径，避免大量无业务价值的访问记录。
 */
@Configuration
@RequiredArgsConstructor
public class SystemLogWebConfig implements WebMvcConfigurer {

    private final SystemLogInterceptor systemLogInterceptor;

    /**
     * 注册拦截器。
     * <p>
     * 只记录 API 请求，静态图片访问量大且业务价值低，排除后日志更干净。
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(systemLogInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/admin-uploads/**", "/mall-uploads/**", "/community-products/**");
    }
}
