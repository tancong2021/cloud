
package com.tancong.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * ==============================
 * 〈全局 CORS（跨域资源共享）配置类，
 *  它用来解决 前端（如 Vue/React）访问后端接口时报的“跨域请求被拒绝”问题类〉
 * ==============================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/03
 */
@Configuration
public class CorsConfig {
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1允许任何域名使用
        corsConfiguration.addAllowedHeader("*"); // 2允许任何头
        corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
        // corsConfiguration.setAllowCredentials(true); // 是否带上Cookie
        return corsConfiguration;
    }

    @Bean("corsFilter")
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
