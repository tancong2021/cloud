package com.tancong.security.aspect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tancong.common.entity.vo.RespBody;
import com.tancong.security.service.EncryptService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * ===================================
 * 切面类（Aspect）- 响应加密
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/03
 */
@Aspect
@Component
@Slf4j
public class EncryptAspect {

    // ✅ 优化：复用 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 方法返回后执行
     * @param joinPoint 连接点
     * @param res 方法返回值
     */
    @AfterReturning(
            value = "@annotation(com.tancong.security.annotation.Encrypt)",
            returning = "res"
    )
    @SuppressWarnings("unchecked")  // ✅ 抑制泛型警告
    public void after(JoinPoint joinPoint, Object res) { // 普通连接点
        // 1. 检查返回值是否为 RespBody
        if (Objects.isNull(res) || !(res instanceof RespBody)) {
            log.warn("@Encrypt 注解只能用于返回 RespBody 的方法");
            return;
        }
        RespBody<?> respBody = (RespBody<?>) res;
        Object data = respBody.getData();

        // 2. 如果 data 为 null，跳过
        if (Objects.isNull(data)) {
            log.debug("返回数据为 null，跳过加密");
            return;
        }

        try {
            String jsonData;

            // 3. 根据类型处理
            if (data instanceof String) {
                // 3.1 String 类型：直接使用
                jsonData = (String) data;
            } else {
                // 3.2 对象类型：序列化成 JSON
                jsonData = objectMapper.writeValueAsString(data);
            }

            // 4. 加密
            String encrypted = EncryptService.encrypt(jsonData);

            // 5. 替换 data
            // ✅ 强制类型转换
            ((RespBody<Object>) respBody).setData(encrypted);

            log.debug("响应数据加密成功，方法：{}", joinPoint.getSignature().getName());

        } catch (Exception e) {
            log.error("响应数据加密失败：{}", e.getMessage(), e);
            // 可以选择抛出异常或返回原始数据
            // throw new RuntimeException("数据加密失败", e);
        }
    }
}
