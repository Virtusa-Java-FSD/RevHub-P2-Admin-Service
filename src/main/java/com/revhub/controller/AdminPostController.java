package com.revhub.controller;

import com.revhub.dto.MessageResponse;
import com.revhub.model.Post;
import com.revhub.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// @PreAuthorize("hasAuthority('ADMIN')") // Handled by SecurityConfig
public class AdminPostController {
    private final AdminPostService adminPostService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = adminPostService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{authorId}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable Long authorId) {
        List<Post> posts = adminPostService.getPostsByAuthor(authorId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        Post post = adminPostService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable String id) {
        adminPostService.deletePost(id);
        return ResponseEntity.ok(new MessageResponse("Post deleted successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPostStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        stats.put("totalPosts", adminPostService.getTotalPosts());
        stats.put("postsToday", adminPostService.getPostsCreatedAfter(today));
        stats.put("postsThisWeek", adminPostService.getPostsCreatedAfter(weekAgo));
        stats.put("postsThisMonth", adminPostService.getPostsCreatedAfter(monthAgo));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String q) {
        List<Post> posts = adminPostService.searchPosts(q);
        return ResponseEntity.ok(posts);
    }
}
