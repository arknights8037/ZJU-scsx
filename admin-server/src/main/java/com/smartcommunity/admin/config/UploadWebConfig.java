package com.smartcommunity.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 把商品图片目录映射为 /admin-uploads/**。
 */
@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    private final Path uploadRoot;

    public UploadWebConfig(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin-uploads/**")
            .addResourceLocations(uploadRoot.toUri().toString());
    }
}
