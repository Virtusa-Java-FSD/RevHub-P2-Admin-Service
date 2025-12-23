package com.revhub.service;
import com.revhub.model.Post;
import com.revhub.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AdminPostService {
    private final PostRepository postRepository;
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    public List<Post> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }
    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }
    @Transactional
    public void deletePost(String postId) {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }
    public long getTotalPosts() {
        return postRepository.count();
    }
    public long getPostsCreatedAfter(LocalDateTime date) {
        return postRepository.countByCreatedAtAfter(date);
    }
    public List<Post> searchPosts(String query) {
        return postRepository.findByContentContainingIgnoreCase(query);
    }
}
