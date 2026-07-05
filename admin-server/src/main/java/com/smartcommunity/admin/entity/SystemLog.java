package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统访问/操作日志实体，对应 system_log 表。
 *
 * 由拦截器自动写入，覆盖管理端和居民端的所有 /api/** 请求。
 */
@Data
@TableName("system_log")
public class SystemLog {
    @TableId(type = IdType.AUTO)
    private Long id; // 日志ID（主键，自增）
    private Integer userId; // 操作用户ID
    private String requestMethod; // 请求方法（GET/POST/PUT/DELETE）
    private String requestUri; // 请求URI
    private String queryString; // 请求查询参数
    private String operationModule; // 操作模块
    private String operationName; // 操作名称
    private String clientIp; // 客户端IP
    private Integer statusCode; // HTTP状态码
    private Boolean success; // 是否成功
    private String errorMessage; // 错误信息
    private Long costMs; // 请求耗时（毫秒）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
}
