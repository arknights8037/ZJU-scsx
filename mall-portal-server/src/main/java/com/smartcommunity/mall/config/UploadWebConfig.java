package com.smartcommunity.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 把本地 uploads 目录映射为浏览器可以访问的 /mall-uploads/**。
 */
@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    private final Path uploadRoot;

    public UploadWebConfig(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mall-uploads/**")
            .addResourceLocations(uploadRoot.toUri().toString());
    }
}
