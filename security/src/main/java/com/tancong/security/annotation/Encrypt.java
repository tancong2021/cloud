package com.tancong.security.annotation;

import java.lang.annotation.*;

/**
 * ===================================
 * 加密标记注解，靠AOP实现自动加密
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/29
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {
}
