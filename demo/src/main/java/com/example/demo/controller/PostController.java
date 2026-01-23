package com.example.demo.controller;

import com.example.demo.dto.PostDetailResponse;
import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        logger.info("获取所有博客文章列表");
        List<Post> posts = postRepository.findAll();
        List<PostResponse> responses = posts.stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
        logger.info("成功获取 {} 篇文章", posts.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPostById(@PathVariable Long id) {
        logger.info("获取文章详情，ID: {}", id);
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            logger.warn("文章不存在，ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        PostDetailResponse response = convertToPostDetailResponse(post);
        logger.info("成功获取文章: {}", post.getTitle());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostDetailResponse> createPost(@Valid @RequestBody PostRequest postRequest) {
        logger.info("创建新文章: {}", postRequest.getTitle());
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setExcerpt(postRequest.getExcerpt());
        post.setContent(postRequest.getContent());
        post.setTags(postRequest.getTags());

        Post savedPost = postRepository.save(post);
        PostDetailResponse response = convertToPostDetailResponse(savedPost);
        logger.info("文章创建成功，ID: {}", savedPost.getId());
        return ResponseEntity.ok(response);
    }

    private PostResponse convertToPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getExcerpt(),
                post.getCreatedAt(),
                post.getTags()
        );
    }

    private PostDetailResponse convertToPostDetailResponse(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getExcerpt(),
                post.getContent(),
                post.getCreatedAt(),
                post.getTags()
        );
    }
}