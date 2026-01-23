package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDetailResponse {
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private LocalDateTime createdAt;
    private List<String> tags;

    public PostDetailResponse() {}

    public PostDetailResponse(Long id, String title, String excerpt, String content, LocalDateTime createdAt, List<String> tags) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.createdAt = createdAt;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
