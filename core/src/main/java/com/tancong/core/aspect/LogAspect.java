package com.tancong.core.aspect;

import cn.hutool.json.JSONUtil;
import com.tancong.common.annotation.LogRecord;
import com.tancong.common.entity.Log;
import com.tancong.common.service.LoggerService;
import com.tancong.common.utils.ServletUtils;
import com.tancong.core.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日志切面类，给加了自定义的日志注解方法进行切面方法环绕
 * @author tancong
 * @create 2025/11/1
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    @Autowired
    private LoggerService loggerService;

    /**
     * 定义切点：在标注了 @LogRecord 注解的方法上生效
     */
    @Pointcut("@annotation(com.tancong.common.annotation.LogRecord)")
    public void logPointCut() { }

    /**
     * 环绕通知：记录方法执行日志
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 创建日志对象
        Log esLog = new Log();

        // 设置请求 IP
        esLog.setIp(ServletUtils.getClientIP(ServletUtils.getRequest()));

        // 设置请求者
        if (ServletUtils.getRequest().getUserPrincipal() instanceof LoginUser user) {
            esLog.setUserId(user.getId());
        }

        // 设置请求参数
        esLog.setContent(JSONUtil.toJsonStr(joinPoint.getArgs()));

        // 获取操作名称（从注解中获取）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogRecord annotation = signature.getMethod().getAnnotation(LogRecord.class);
        // ✅ 使用自定义注解
        esLog.setTitle(annotation != null ? annotation.value() : "未知操作");

        // 设置被调用的方法名称
        String className = joinPoint.getSignature().getDeclaringTypeName();
        esLog.setMethod(className + "." + joinPoint.getSignature().getName());

        // 执行方法
        Object returnObj;
        try {
            returnObj = joinPoint.proceed();
            esLog.setType("INFO");
            log.info("操作日志：{}", esLog);
        } catch (Exception e) {
            esLog.setType("ERROR");
            esLog.setError(e.getMessage());
            log.error("操作异常：{}", esLog, e);
            throw e;
        } finally {
            // 设置请求耗时
            esLog.setTime((int) (System.currentTimeMillis() - start));
            esLog.setCreateTime(LocalDateTime.now());
            loggerService.insert(esLog);
        }

        return returnObj;
    }
}
