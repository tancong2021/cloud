package com.tancong.security.config;

import com.tancong.security.filter.JwtAuthenticationTokenFilter;
import com.tancong.security.handler.AccessDeniedHandlerImpl;
import com.tancong.security.handler.AuthenticationEntryPointImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 密码加密处理器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("SecurityBeanConfig配置类的BCryptPasswordEncoder 已加载");
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP安全配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 启用 CORS
                .cors(cors -> {})

                // 禁用 CSRF（前后端分离项目不需要）
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ 关键：配置无状态会话（JWT 不需要 Session）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ 可选：禁用表单登录和 HTTP Basic
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 添加 JWT 认证过滤器（在 UsernamePasswordAuthenticationFilter 之前）
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)

                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)  // 处理 401
                        .accessDeniedHandler(accessDeniedHandler)           // 处理 403
                )

                // 配置访问权限
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 预检请求（CORS）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 公开接口（根据你的实际路径调整）
                        .requestMatchers("/auth/login","/auth/logout").permitAll()      // 登录
                        .anyRequest().authenticated()
                );

        log.info("Spring Security 配置已加载");
        return http.build();
    }

    /**
     * ✅ 可选：配置静态资源不拦截（提升性能）
     */
    /*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**");
    }
    */
}
