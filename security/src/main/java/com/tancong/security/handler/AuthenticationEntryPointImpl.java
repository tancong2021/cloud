package com.tancong.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tancong.common.entity.vo.RespBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * ===================================
 * 认证失败处理器
 * 用户未登录或登录失败时触发
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("认证失败: {}", authException.getMessage());

        // 根据不同的异常类型返回不同的错误信息
        String message;
        if (authException instanceof DisabledException) {
            message = authException.getMessage();  // 用户被禁用/删除的具体信息
        } else {
            message = "认证失败，请重新登录";
        }

        // 返回 JSON 响应
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        RespBody<Void> result = RespBody.fail(message);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}