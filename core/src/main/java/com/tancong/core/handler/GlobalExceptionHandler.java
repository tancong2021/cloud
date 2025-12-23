package com.tancong.core.handler;

import com.tancong.common.entity.vo.RespBody;
import com.tancong.common.exception.CanShowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * ===================================
 * 全局异常处理器
 * 处理 Controller 层的业务异常
 *
 * 注意：认证/授权异常由 Spring Security 的
 * AuthenticationEntryPoint 和 AccessDeniedHandler 处理
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常
     * 包括：业务异常、参数校验异常、数据库异常等
     */
    @ExceptionHandler(Exception.class)
    public RespBody<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return RespBody.fail("系统异常，请稍后重试");
    }

    @ExceptionHandler(CanShowException.class)
    public ResponseEntity<RespBody> catchCanShowException(CanShowException e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResponseEntity<>(RespBody.fail(e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public RespBody<String> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("文件大小超过限制", e);
        return RespBody.fail("文件大小超过限制（最大100MB）");
    }

}
