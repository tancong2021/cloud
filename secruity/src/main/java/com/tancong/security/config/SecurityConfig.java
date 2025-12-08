package com.tancong.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ===================================
 * Spring Security配置类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/03
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    /**
     * 密码加密处理器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("SecurityBeanConfig配置类的BCryptPasswordEncoder 已加载");
        return new BCryptPasswordEncoder(); // 返回 PasswordEncoder 接口
    }
}
