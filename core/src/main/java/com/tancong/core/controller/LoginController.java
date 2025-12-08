package com.tancong.core.controller;

import com.tancong.common.entity.vo.RespBody;
import com.tancong.core.entity.LoginUser;
import com.tancong.core.entity.dto.LoginUserDTO;
import com.tancong.core.service.impl.UserService;
import com.tancong.common.annotation.API;
import com.tancong.core.service.impl.TokenService;
import com.tancong.core.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ===================================
 * 登入接口控制层
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/26
 */
@API(path="",value = "loginController", name = "登入API", description = "登入接口控制层")
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
    @PostMapping("/login")
    public RespBody<String> login(LoginUserDTO loginUserDTO,
                                  HttpServletResponse response) {

        // 1️⃣ 游客模式登录
        if ("guest".equals(loginUserDTO.getUsername())) {
            return guestLogin(loginUserDTO ,response);
        }

        // 2️⃣ 正常用户登录
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
     * 游客登录逻辑
     */
    private RespBody<String> guestLogin(LoginUserDTO loginUserDTO, HttpServletResponse response) {

        // 从数据库或者配置加载游客用户
        LoginUser guestUser = userService.loadGuestUser(); // 确保不会用 static 缓存

        if (guestUser == null) {
            return RespBody.fail("游客账号未启用");
        }

        // 加载用户详细信息（权限、菜单等）
        userDetailsService.loadUserInfo(guestUser);

        // 根据登入用户生成 JWT
        String token = tokenService.createToken(guestUser);

        // 返回 token
        return RespBody.success(token);
    }
}
