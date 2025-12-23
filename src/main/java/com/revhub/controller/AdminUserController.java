package com.revhub.controller;

import com.revhub.dto.AdminStatsResponse;
import com.revhub.dto.BanUserRequest;
import com.revhub.model.User;
import com.revhub.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// @PreAuthorize("hasAuthority('ADMIN')") // Handled by SecurityConfig
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = adminUserService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/banned")
    public ResponseEntity<List<User>> getBannedUsers() {
        List<User> users = adminUserService.getBannedUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<User>> getDeletedUsers() {
        List<User> users = adminUserService.getDeletedUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = adminUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/ban")
    public ResponseEntity<User> banUser(@PathVariable Long id, @RequestBody BanUserRequest request) {
        User user = adminUserService.banUser(id, request.getReason());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/unban")
    public ResponseEntity<User> unbanUser(@PathVariable Long id) {
        User user = adminUserService.unbanUser(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> softDeleteUser(@PathVariable Long id) {
        User user = adminUserService.softDeleteUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<User> restoreUser(@PathVariable Long id) {
        User user = adminUserService.restoreUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getUserStats() {
        AdminStatsResponse stats = adminUserService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String q) {
        List<User> users = adminUserService.searchUsers(q);
        return ResponseEntity.ok(users);
    }
}
