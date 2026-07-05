package com.smartcommunity.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类。
 * <p>
 * 配置分页插件，使 MyBatis-Plus 的分页查询支持 MySQL 方言。
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 注册 MyBatis-Plus 分页拦截器。
     *
     * @return 配置好的 MyBatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor i = new MybatisPlusInterceptor();
        // 添加 MySQL 分页拦截器，支持自动计算总记录数
        i.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return i;
    }
}
