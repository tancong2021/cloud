package com.tancong.security.filter;

import com.tancong.security.entity.AuthUser;
import com.tancong.security.service.BaseTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ===================================
 * JWT认证过滤器
 * ===================================
 * 从请求头中提取JWT Token，验证并设置用户认证信息到SecurityContext
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/12
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private  BaseTokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从请求头中获取 Token
        String token = getTokenFromRequest(request);

        // 2. 如果 Token 存在，进行验证
        if (StringUtils.hasText(token)) {
            try {
                // 验证并获取用户信息
                AuthUser loginUser = tokenService.verifyToken(token);

                if (loginUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    loginUser,
                                    null,
                                    loginUser.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT认证成功:username={}", loginUser.getUsername());
                }
            } catch (Exception e) {
                log.warn("JWT Token验证失败: {}", e.getMessage());
            }
        }

        // 3. 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
