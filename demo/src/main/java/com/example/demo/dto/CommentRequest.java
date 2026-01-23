package com.example.demo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50字符")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;

    @NotBlank(message = "留言内容不能为空")
    @Size(min = 10, max = 1000, message = "留言内容长度必须在10-1000字符之间")
    private String message;

    public CommentRequest() {}

    public CommentRequest(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
