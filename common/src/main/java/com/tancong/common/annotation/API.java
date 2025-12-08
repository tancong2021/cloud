package com.tancong.common.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * ===================================
 * 一个自定义注解类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Documented  // 表示这个注解应该被包含在Javadoc中
@RestController
@RequestMapping
@Target(ElementType.TYPE) // 自定义注解只能用在类级别
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "") //用于API分组和文档分类
public @interface API {
    /**
     * 控制器的基础路径
     * 等价于 @RequestMapping(path = "/xxx")
     */
    // 这里只能一对一映射，就是一个注解方法只能映射另一个注解属性的别名
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};

    /**
     * 控制器 bean 名称（可选）
     * 等价于 @RestController(value = "beanName")
     */
    @AliasFor(annotation = RestController.class, attribute = "value")
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
