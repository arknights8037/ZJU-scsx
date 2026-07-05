package com.smartcommunity.mall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器（商城端 - 已弃用）。
 * <p>
 * 门户端代码并入 admin-server 后，统一使用
 * com.smartcommunity.admin.common.GlobalExceptionHandler。
 * 保留此类仅作旧包源码参考。
 * <p>
 * 注意：此类未标注 @RestControllerAdvice，避免两个全局异常处理器同时生效。
 */
@Slf4j
/*
 * 门户端代码并入 admin-server 后，统一使用
 * com.smartcommunity.admin.common.GlobalExceptionHandler。
 * 保留此类仅作旧包源码参考，避免两个 @RestControllerAdvice 同时处理同一异常。
 */
public class GlobalExceptionHandler {

    /**
     * 处理 RuntimeException 及其子类。
     *
     * @param e 运行时异常
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("Runtime error: ", e);
        return Result.fail(e.getMessage());
    }

    /**
     * 处理未捕获的 Exception。
     *
     * @param e 通用异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("System error: ", e);
        return Result.fail("系统繁忙，请稍后再试");
    }
}
