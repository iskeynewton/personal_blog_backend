package com.example.demo.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "密码不能为空")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
