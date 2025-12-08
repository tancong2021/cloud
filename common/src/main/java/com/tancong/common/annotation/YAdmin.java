package com.tancong.common.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * ===================================
 * 后台控制台admin控制层的注解
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Controller
@RequestMapping
@Tag(name = "")
public @interface YAdmin {
    /**
     * 控制器的基础路径
     */
    @AliasFor(attribute = "path", annotation = RequestMapping.class)
    String[] path() default {};

    /**
     * 控制器 bean 名称（可选）丢给容器管理
     * 等价于 @Controller(value = "beanName")
     */
    @AliasFor(attribute = "value", annotation = Controller.class)
    String value() default "";

    /**
     * Swagger 接口组名称
     */
    @AliasFor(annotation = Tag.class, attribute = "name")
    String name() default "";

    /**
     * Swagger 接口描述
     */
    @AliasFor(annotation = Tag.class, attribute = "description")
    String description() default "";
}