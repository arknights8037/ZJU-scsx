package com.smartcommunity.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 统一后端启动类。
 *
 * admin 包负责物业管理后台，mall 包负责居民商城/社区门户。两个包里存在同名
 * Mapper、Controller、Result 等类，因此这里显式配置扫描范围和 Bean 命名策略，
 * 让合并后的单体服务可以同时承载两套路由与定时任务。
 */
@SpringBootApplication(
    scanBasePackages = {"com.smartcommunity.admin", "com.smartcommunity.mall"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@MapperScan(
    basePackages = {"com.smartcommunity.admin.mapper", "com.smartcommunity.mall.mapper"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableScheduling
public class AdminApplication {

    /**
     * 应用启动入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
