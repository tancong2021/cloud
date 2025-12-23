package com.tancong.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tancong.common.entity.vo.RespBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ===================================
 * 权限拒绝处理器
 * 用户已认证但权限不足时触发
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/11
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn("权限不足: URI={}, ExceptionType={}",
                 request.getRequestURI(),
                 accessDeniedException.getClass().getSimpleName());

        // 返回 JSON 响应
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403
        response.setContentType("application/json;charset=UTF-8");

        RespBody<Void> result = RespBody.fail("权限不足，无法访问该资源");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
