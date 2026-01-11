package com.tancong.security.service;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.tancong.security.config.YamlSourceFactory;
import com.tancong.security.entity.AuthUser;
import com.tancong.security.entity.enums.TokenType;
import com.tancong.common.utils.CacheManagers;
import com.tancong.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * ===================================
 * 提供Token基本服务的类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
@Slf4j
@Data
@Component
@PropertySource(value = "classpath:security.yml", factory = YamlSourceFactory.class)
public class BaseTokenService {
    // private static final Logger log = LogManager.getLogger(BaseTokenService.class);
    // 存放 Token 的头部名称
    @Value("${spring.security.token.header}")
    private String AUTH_HEADER;
    // Token 前缀
    @Value("${spring.security.token.prefix}")
    private String TOKEN_PREFIX;

    // 用于加密的密钥
    private String SECRET_KEY;

    // Token 的 claim 信息
    protected final String JWT_CLAIM_UUID = "UUID";
    // 设置 Token 到期时间---单位是毫秒
    private long expiresMillis;
    // Token 验证器
    private JWTVerifier verifier = null;


    @Value("${spring.security.token.expires-minutes:30}")
    public void setEXPIRES(int expiresMinutes) {
        this.expiresMillis = TimeUnit.MINUTES.toMillis(expiresMinutes);
    }

    @Value("${spring.security.token.secret-key}")
    public void setSECRET_KEY(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
        this.verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
    }

    /**
     * 创建 Token
     */
    public String createToken(String uuid, TokenType type, @NotNull AuthUser user) {
        Date expireTime = new Date(System.currentTimeMillis() + expiresMillis);
        String token = JWT.create()
                .withAudience(uuid)
                .withClaim("TYPE", type.name())
                .withClaim(JWT_CLAIM_UUID, uuid)
                .withExpiresAt(expireTime)
                .sign(Algorithm.HMAC256(SECRET_KEY));
        user.setExpireTime(expireTime);
        CacheManagers.set(uuid, user, user.expired());
        return token;
    }

    /**
     * 获取请求头中的 token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(AUTH_HEADER);
        return Objects.isNull(token) ? "" : token.replace(TOKEN_PREFIX, "").trim();
    }

    /**
     * ✅ 新增：验证 Token 并返回用户信息（用于过滤器）
     */
    public AuthUser verifyToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }

        try {
            // 验证 Token 签名和过期时间
            verifier.verify(token);

            // 从缓存中获取用户信息
            String uuid = getUUID(token);
            AuthUser user = (AuthUser) CacheManagers.get(uuid);

            if (user == null) {
                log.warn("Token 验证失败：缓存中未找到用户信息, uuid={}", uuid);
                return null;
            }

            return user;

        } catch (TokenExpiredException e) {
            String uuid = getUUID(token);
            CacheManagers.del(uuid);
            log.warn("Token 已过期, uuid={}", uuid);
            return null;
        } catch (Exception e) {
            log.error("Token 验证异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 校验 Token 是否有效（只返回 boolean）
     */
    public boolean isTokenValid(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            verifier.verify(token);
            return true;
        } catch (TokenExpiredException e) {
            CacheManagers.del(this.getUUID(token));
            log.error("Token已过期，请重新登录 >>> {}", e.getMessage());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getClaim(HttpServletRequest request, String name) {
        return this.getClaim(this.getToken(request), name);
    }

    public String getClaim(String token, String name) {
        if (StrUtil.isBlank(token)) {
            log.info("token为空");
            return null;
        }
        try {
            return JWT.decode(token).getClaim(name).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public TokenType getType(String token) {
        try {
            String name = JWT.decode(token).getClaim("TYPE").asString();
            return TokenType.valueOf(name);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 从请求中获取UUID字符串
     */
    public String getUUID(HttpServletRequest request) {
        try {
            return getUUID(getToken(request));
        } catch (JWTDecodeException e) {
            return "";
        }
    }

    /**
     * 从Token中获取UUID字符串
     */
    public String getUUID(String token) {
        try {
            return JWT.decode(token).getClaim(JWT_CLAIM_UUID).asString();
        } catch (JWTDecodeException e) {
            return "";
        }
    }

    /**
     * 从内存中获取 UserDetails 信息
     */
    public AuthUser getAuthUser() {
        return this.getAuthUser(ServletUtils.getRequest());
    }

    /**
     * 从内存中获取 UserDetails 信息
     */
    public AuthUser getAuthUser(HttpServletRequest request) {
        String token = this.getToken(request);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return (AuthUser) CacheManagers.get(this.getUUID(token));
    }
}
