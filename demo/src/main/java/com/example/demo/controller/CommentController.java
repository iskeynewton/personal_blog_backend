package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        logger.info("获取所有留言列表");
        List<Comment> comments = commentRepository.findAll();
        List<CommentResponse> responses = comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
        logger.info("成功获取 {} 条留言", comments.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        logger.info("收到新留言: {} ({})", commentRequest.getName(), commentRequest.getEmail());
        Comment comment = new Comment();
        comment.setName(commentRequest.getName());
        comment.setEmail(commentRequest.getEmail());
        comment.setMessage(commentRequest.getMessage());

        Comment savedComment = commentRepository.save(comment);
        CommentResponse response = convertToCommentResponse(savedComment);
        logger.info("留言保存成功，ID: {}", savedComment.getId());
        return ResponseEntity.ok(response);
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getName(),
                comment.getEmail(),
                comment.getMessage(),
                comment.getCreatedAt()
        );
    }
}