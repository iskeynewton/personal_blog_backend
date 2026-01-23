package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;

    @Value("${admin.password}")
    private String adminPassword;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("管理员登录尝试");

        if (!adminPassword.equals(loginRequest.getPassword())) {
            logger.warn("管理员登录失败：密码错误");
            return ResponseEntity.badRequest().build();
        }

        String token = jwtUtil.generateToken("admin");
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername("admin");
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(userInfo);

        logger.info("管理员登录成功，生成JWT令牌");
        return ResponseEntity.ok(response);
    }
}
