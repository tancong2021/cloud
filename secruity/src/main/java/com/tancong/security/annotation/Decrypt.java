package com.tancong.security.annotation;

/**
 * ===================================
 * 解密标记注解，靠AOP实现自动解密
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/29
 */

import java.lang.annotation.*;

/**
 * 打在 Controller 方法上：整个请求体解密
 * 打在参数上：仅解密该参数
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decrypt {
}
