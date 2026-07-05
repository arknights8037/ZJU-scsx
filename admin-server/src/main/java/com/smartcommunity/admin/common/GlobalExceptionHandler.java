package com.smartcommunity.admin.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 * <p>
 * 使用 @RestControllerAdvice 统一捕获 Controller 层抛出的异常，
 * 将其转换为统一的 Result 格式返回，避免直接返回原始错误堆栈。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 RuntimeException 及其子类异常。
     *
     * @param e 运行时异常
     * @return 包含错误信息的统一响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntime(RuntimeException e) {
        log.error("Runtime error: ", e);
        return Result.fail(e.getMessage());
    }

    /**
     * 处理其他未捕获的 Exception。
     *
     * @param e 通用异常
     * @return 统一的"系统繁忙"响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handle(Exception e) {
        log.error("System error: ", e);
        return Result.fail("系统繁忙");
    }
}
