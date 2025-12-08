package com.tancong.core.service.impl;


import cn.hutool.core.util.IdUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tancong.common.utils.CacheManagers;
import com.tancong.common.utils.ServletUtils;
import com.tancong.core.entity.LoginUser;
import com.tancong.security.entity.AuthUser;
import com.tancong.security.entity.ShareUser;
import com.tancong.security.entity.enums.TokenType;
import com.tancong.security.service.BaseTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ===================================
 * <p>
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/03
 */
@Log4j2
@Component("tokenService")
public class TokenService extends BaseTokenService {

    /**
     * 根据登入用户创建 Token
     * @param loginUser
     * @return
     */
    public String createToken(LoginUser loginUser) {
        String loginId = IdUtil.simpleUUID();
        loginUser.setLoginId(loginId);
        return super.createToken(loginId, TokenType.LOGIN, loginUser);
    }

    public String createShareToken(String shareId) {
        String uuid = IdUtil.simpleUUID();
        Date expireTime = new Date(System.currentTimeMillis() + super.getExpiresMillis());
        String token = JWT.create()
                .withAudience(shareId)
                .withClaim("TYPE", TokenType.SHARE.name())
                .withClaim("shareId", shareId)
                .withClaim(JWT_CLAIM_UUID, uuid)
                .withExpiresAt(expireTime)
                .sign(Algorithm.HMAC256(super.getSECRET_KEY()));
        ShareUser shareUser = new ShareUser(shareId);
        shareUser.setExpireTime(expireTime);
        CacheManagers.set(uuid, shareUser, shareUser.expired());
        return token;
    }

    public LoginUser getLoginUser() {
        return this.getLoginUser(ServletUtils.getRequest());
    }

    public LoginUser getLoginUser(HttpServletRequest request) {
        AuthUser authUser = super.getAuthUser(request);
        if (authUser instanceof LoginUser) return (LoginUser) authUser;
        return null;
    }

    public TokenType getType(HttpServletRequest request) {

        String type = super.getClaim(request, "TYPE");

        // ✅ 处理 null 和无效值
        if (type == null) {
            return TokenType.DEFAULT;  // 返回默认值
        }

        try {
            return TokenType.valueOf(type);
        } catch (IllegalArgumentException e) {
            log.warn("无效的 TokenType: {}, 使用默认值", type);
            return TokenType.DEFAULT;
        }
    }

}
