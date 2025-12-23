package com.revhub.service;
import com.revhub.dto.AdminStatsResponse;
import com.revhub.model.User;
import com.revhub.model.UserRole;
import com.revhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<User> getActiveUsers() {
        return userRepository.findAllActive();
    }
    public List<User> getBannedUsers() {
        return userRepository.findByIsBanned(true);
    }
    public List<User> getDeletedUsers() {
        return userRepository.findByIsDeleted(true);
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    @Transactional
    public User banUser(Long userId, String reason) {
        User user = getUserById(userId);
        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Cannot ban admin users");
        }
        user.setIsBanned(true);
        user.setBannedAt(LocalDateTime.now());
        user.setBanReason(reason);
        return userRepository.save(user);
    }
    @Transactional
    public User unbanUser(Long userId) {
        User user = getUserById(userId);
        user.setIsBanned(false);
        user.setBannedAt(null);
        user.setBanReason(null);
        return userRepository.save(user);
    }
    @Transactional
    public User softDeleteUser(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Cannot delete admin users");
        }
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    @Transactional
    public User restoreUser(Long userId) {
        User user = getUserById(userId);
        user.setIsDeleted(false);
        user.setDeletedAt(null);
        return userRepository.save(user);
    }
    public AdminStatsResponse getUserStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        // User statistics
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByIsDeleted(false));
        stats.setBannedUsers(userRepository.countByIsBanned(true));
        stats.setDeletedUsers(userRepository.countByIsDeleted(true));
        // New users statistics
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        stats.setNewUsersToday(userRepository.countUsersCreatedAfter(today));
        stats.setNewUsersThisWeek(userRepository.countUsersCreatedAfter(weekAgo));
        stats.setNewUsersThisMonth(userRepository.countUsersCreatedAfter(monthAgo));
        return stats;
    }
    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }
}
