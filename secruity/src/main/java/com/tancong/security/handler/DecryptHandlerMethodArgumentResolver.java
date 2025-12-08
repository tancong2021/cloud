package com.tancong.security.handler;


import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tancong.security.annotation.Decrypt;
import com.tancong.security.service.EncryptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * ===================================
 * 参数解析器
 * 作用：拦截并处理带有 @Decrypt 注解的参数
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/03
 */
@Slf4j
@Component
public class DecryptHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    // ✅ 复用 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 请求体缓存的属性名
    private static final String CACHED_REQUEST_BODY_ATTR = "CACHED_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Decrypt.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        try {
            // 1. 读取请求体
            String requestBody = getRequestBody(webRequest);
            log.debug("接收到加密请求，长度：{}", requestBody.length());

            // 2. 解析成 EncryptData 对象
            EncryptData encryptData = JSONUtil.toBean(requestBody, EncryptData.class);

            if (encryptData == null || encryptData.getData() == null) {
                throw new IllegalArgumentException("请求体格式错误，期望：{\"data\":\"加密字符串\"}");
            }

            // 3. 解密
            String decryptedJson = EncryptService.decrypt(encryptData.getData());
            log.debug("解密成功，JSON：{}", decryptedJson);

            // 4. 获取目标类型
            Class<?> targetType = parameter.getParameterType();

            // 5. 反序列化成目标对象
            Object result = objectMapper.readValue(decryptedJson, targetType);

            log.debug("参数解析成功，类型：{}", targetType.getSimpleName());

            return result;

        } catch (Exception e) {
            log.error("参数解密失败：{}", e.getMessage(), e);
            throw new RuntimeException("请求参数解密失败：" + e.getMessage(), e);
        }
    }

    /**
     * 读取请求体（带缓存）
     */
    private String getRequestBody(NativeWebRequest webRequest) throws IOException {
        // 1. 尝试从缓存获取
        Object cached = webRequest.getAttribute(CACHED_REQUEST_BODY_ATTR, NativeWebRequest.SCOPE_REQUEST);
        if (cached != null) {
            return (String) cached;
        }

        // 2. 获取 HttpServletRequest
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            throw new IllegalStateException("无法获取 HttpServletRequest");
        }

        // 3. 读取请求体
        String requestBody = IoUtil.read(
                servletRequest.getInputStream(),
                StandardCharsets.UTF_8
        );

        // 4. 缓存请求体（防止重复读取）
        webRequest.setAttribute(CACHED_REQUEST_BODY_ATTR, requestBody, NativeWebRequest.SCOPE_REQUEST);

        return requestBody;
    }

    /**
     * 加密数据格式
     */
    @Data
    static class EncryptData {
        private String data;
    }
}