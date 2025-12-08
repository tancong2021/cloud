package com.tancong.common.annotation;

import java.lang.annotation.*;

/**
 * @author tancong
 * @create 2025/10/31
 * @since 1.0.0
 * 操作日志注解
 * 用于方法，用于记录操作说明，模块名，是否记录参数等
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecord {
    /**
     * 操作说明：这是一个什么操作
     */
    String value();

    /**
     * 所属模块：用户管理等
     */
    String module() default "";
    /**
     * 是否打印方法入参
     */
    boolean logParams() default true;

    /**
     * 是否打印方法返回结果
     */
    boolean logResult() default true;
}
