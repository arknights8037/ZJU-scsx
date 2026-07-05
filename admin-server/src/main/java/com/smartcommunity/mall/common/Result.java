package com.smartcommunity.mall.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 统一 API 响应结果封装类（商城端）。
 * <p>
 * 所有 Controller 返回统一格式，包含业务状态码、消息和数据。
 * 兼容老师示例工程中的 code/msg/data 响应字段命名（通过 @JsonProperty 映射）。
 *
 * @param <T> 数据体类型
 */
@Data
public class Result<T> {

    /** 业务状态码，200 表示成功 */
    private Integer code;
    /** 提示消息，同时映射为 msg 字段 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 私有构造方法。
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
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（无数据）。
     *
     * @param <T> 数据类型
     * @return Result 实例
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 失败响应（默认 500 状态码）。
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败响应（自定义状态码）。
     *
     * @param code    业务状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 未授权响应（401）。
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 实例
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    /**
     * 兼容老师示例工程中的 code/msg/data 响应字段。
     * message 字段在序列化时同时映射为 msg。
     */
    @JsonProperty("msg")
    public String getMsg() {
        return message;
    }

    @JsonProperty("msg")
    public void setMsg(String msg) {
        this.message = msg;
    }
}
