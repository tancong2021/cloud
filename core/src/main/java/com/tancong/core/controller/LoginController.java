package com.tancong.core.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tancong.common.annotation.LogRecord;
import com.tancong.common.entity.vo.RespBody;
import com.tancong.common.exception.CanShowException;
import com.tancong.common.utils.CacheManagers;
import com.tancong.core.entity.LoginUser;
import com.tancong.core.entity.dto.LoginUserDTO;
import com.tancong.core.service.impl.UserService;
import com.tancong.common.annotation.API;
import com.tancong.core.service.impl.TokenService;
import com.tancong.core.service.impl.UserDetailsServiceImpl;
import com.tancong.security.annotation.Decrypt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ===================================
 * 登入接口控制层
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@Slf4j
@API(path="/auth",value = "loginController", name = "登入API", description = "认证接口控制层")
public class LoginController {
    @Autowired
    AuthenticationManagerBuilder managerBuilder;

    @Autowired
    UserService userService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    TokenService tokenService;
    /**
     * 登录接口
     */
    @LogRecord(value = "用户登入", module = "认证模块")
    @PostMapping("/login") // 这里参数应该加上@Decrypt解密的注解
    public RespBody<String> login(@RequestBody LoginUserDTO loginUserDTO) {
        // 正常用户登录
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword());
        Authentication authentication =
                managerBuilder.getObject().authenticate(authToken);

        // 登录成功
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 加载用户详细信息（权限、菜单等）
        userDetailsService.loadUserInfo(loginUser);

        // 根据登入生成 JWT Token
        String token = tokenService.createToken(loginUser);

        // 返回 token 给前端
        return RespBody.success(token);
    }

    /**
     *
     * @param request
     * @return
     */
    @LogRecord(value = "用户登出", module = "认证模块")
    @PostMapping("/logout")
    public RespBody<String> logout(HttpServletRequest request) {
        String uuid = tokenService.getUUID(request);

        if (StrUtil.isBlank(uuid)) {
            throw new CanShowException("未找到有效的 Token");
        }

        boolean deleted = CacheManagers.del(uuid);
        if (!deleted) {
            log.warn("Token 删除失败: uuid={}", uuid);
            throw new CanShowException("登出失败，请重试");
        }

        log.info("用户登出成功: uuid={}", uuid);
        return RespBody.success("登出成功");

    }
}
