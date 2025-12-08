package com.tancong.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * ======================================================
 * 主动抛出可直接展示给前端用户的异常信息，同时还能带上 HTTP 状态码
 * ======================================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/02
 */
@Data
public class CanShowException extends RuntimeException {

    private HttpStatus status = HttpStatus.OK;

    public CanShowException(String message) {
        super(message);
    }

    public CanShowException(String title, String message) {
        super(StrUtil.join("###", title ,message));
    }

    public CanShowException(String message, int code) {
        super(message);
        this.status = HttpStatus.valueOf(code);
    }

    public CanShowException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
