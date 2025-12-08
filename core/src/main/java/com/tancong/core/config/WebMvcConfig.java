package com.tancong.core.config;


import com.tancong.security.handler.DecryptHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * ===================================
 * Spring MVC全局配置类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/03
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 需要把自己的解密参数拦截器注册
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new DecryptHandlerMethodArgumentResolver());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
