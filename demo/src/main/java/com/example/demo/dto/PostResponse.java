package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    private Long id;
    private String title;
    private String excerpt;
    private LocalDateTime createdAt;
    private List<String> tags;
    private String image; // new field for thumbnail/image name or URL

    public PostResponse() {}

    public PostResponse(Long id, String title, String excerpt, LocalDateTime createdAt, List<String> tags, String image) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.createdAt = createdAt;
        this.tags = tags;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
