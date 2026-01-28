package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.service.CommentVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentRepository commentRepository;
    private final CommentVerificationService verificationService;

    public CommentController(CommentRepository commentRepository, CommentVerificationService verificationService) {
        this.commentRepository = commentRepository;
        this.verificationService = verificationService;
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

    // Request a verification token to be sent to the provided email (for editing/deleting comments)
    @PostMapping("/verify/request")
    public ResponseEntity<?> requestVerification(@RequestBody(required = true) VerificationRequest req) {
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("email is required");
        }
        String token = verificationService.generateTokenForEmail(req.getEmail(), 10); // 10 minutes validity
        // In a real app we'd send the token via email. For now we log it.
        logger.info("Verification token for {} : {}", req.getEmail(), token);
        return ResponseEntity.ok().build();
    }

    // Verify token for a specific comment id and action (edit/delete). This endpoint just validates the token.
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyToken(@PathVariable Long id, @RequestBody VerifyActionRequest req) {
        if (req.getToken() == null || req.getToken().isEmpty()) {
            return ResponseEntity.badRequest().body("token is required");
        }
        // find comment
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (!optionalComment.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Comment comment = optionalComment.get();
        boolean ok = verificationService.validateToken(comment.getEmail(), req.getToken());
        if (!ok) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return ResponseEntity.ok().build();
    }

    // Update comment: allowed for admin (authenticated) or comment owner validated via token header `X-Comment-Token`.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @Valid @RequestBody UpdateCommentRequest updateRequest,
                                           @RequestHeader(value = "X-Comment-Token", required = false) String token,
                                           Authentication authentication) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (!optionalComment.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Comment comment = optionalComment.get();
        boolean allowed = false;
        if (authentication != null && authentication.isAuthenticated()) {
            // admin
            allowed = true;
        } else if (token != null && !token.isEmpty()) {
            allowed = verificationService.validateToken(comment.getEmail(), token);
        }
        if (!allowed) {
            return ResponseEntity.status(401).body("Not authorized to update this comment");
        }
        if (updateRequest.getMessage() != null) {
            comment.setMessage(updateRequest.getMessage());
        }
        Comment saved = commentRepository.save(comment);
        return ResponseEntity.ok(convertToCommentResponse(saved));
    }

    // Delete comment: allowed for admin or comment owner via token
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                           @RequestHeader(value = "X-Comment-Token", required = false) String token,
                                           Authentication authentication) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (!optionalComment.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Comment comment = optionalComment.get();
        boolean allowed = false;
        if (authentication != null && authentication.isAuthenticated()) {
            allowed = true;
        } else if (token != null && !token.isEmpty()) {
            allowed = verificationService.validateToken(comment.getEmail(), token);
        }
        if (!allowed) {
            return ResponseEntity.status(401).body("Not authorized to delete this comment");
        }
        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
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

    // --- request DTOs used only by controller endpoints ---
    public static class VerificationRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class VerifyActionRequest {
        private String token;
        private String action; // edit or delete

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    public static class UpdateCommentRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}