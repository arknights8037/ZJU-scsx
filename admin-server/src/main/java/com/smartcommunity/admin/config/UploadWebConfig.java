package com.smartcommunity.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 静态资源上传目录映射配置类。
 * <p>
 * 将上传文件的本地目录映射为 /admin-uploads/**、/mall-uploads/** 和
 * /community-products/** 的 HTTP 可访问路径。
 */
@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    /** 上传文件根目录（由 app.upload-dir 配置） */
    private final Path uploadRoot;
    /** 社区商品图片目录（默认在 resources/static 下） */
    private final Path communityProductsRoot;

    /**
     * 构造上传配置。
     *
     * @param uploadDir 上传根目录路径，从配置文件 app.upload-dir 读取
     */
    public UploadWebConfig(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
        this.communityProductsRoot = Path.of("src/main/resources/static/community-products").toAbsolutePath().normalize();
    }

    /**
     * 注册静态资源处理器，将本地目录映射为 URL 路径。
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 后台管理端上传文件
        registry.addResourceHandler("/admin-uploads/**")
            .addResourceLocations(uploadRoot.toUri().toString());
        // 商城端上传文件
        registry.addResourceHandler("/mall-uploads/**")
            .addResourceLocations(uploadRoot.toUri().toString());
        // 社区产品图片（同时支持 classpath 和本地绝对路径）
        registry.addResourceHandler("/community-products/**")
            .addResourceLocations(
                "classpath:/static/community-products/",
                communityProductsRoot.toUri().toString()
            );
    }
}
