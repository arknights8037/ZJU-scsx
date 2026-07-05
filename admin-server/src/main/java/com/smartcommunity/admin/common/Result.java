package com.smartcommunity.admin.common;

import lombok.Data;

/**
 * 统一 API 响应结果封装类。
 * <p>
 * 所有 Controller 方法都返回此类型，保证前端统一解析格式。
 * 包含业务状态码（code）、消息（message）和数据（data）三个字段。
 *
 * @param <T> 数据体类型
 */
@Data
public class Result<T> {
    /** 业务状态码，200 表示成功 */
    private Integer code;
    /** 提示消息 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 私有构造方法，通过静态工厂方法创建实例。
     *
     * @param code    业务状态码
     * @param message 提示消息
     * @param data    响应数据
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（带数据）。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result 实例
     */
    public static <T> Result<T> ok(T data) { return new Result<>(200, "success", data); }

    /**
     * 成功响应（无数据）。
     *
     * @param <T> 数据类型
     * @return Result 实例
     */
    public static <T> Result<T> ok() { return ok(null); }

    /**
     * 失败响应（使用默认 500 状态码）。
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail(String message) { return new Result<>(500, message, null); }

    /**
     * 失败响应（自定义状态码）。
     *
     * @param code    业务状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail(Integer code, String message) { return new Result<>(code, message, null); }
}
