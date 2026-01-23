package com.example.demo.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class PostRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    @NotBlank(message = "摘要不能为空")
    private String excerpt;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotEmpty(message = "标签不能为空")
    private List<String> tags;

    public PostRequest() {}

    public PostRequest(String title, String excerpt, String content, List<String> tags) {
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
