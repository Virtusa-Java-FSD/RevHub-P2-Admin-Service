package com.revhub.service;
import com.revhub.dto.AdminStatsResponse;
import com.revhub.repository.PostRepository;
import com.revhub.repository.ReportRepository;
import com.revhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    public AdminStatsResponse getOverviewStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        // User statistics
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByIsDeleted(false));
        stats.setBannedUsers(userRepository.countByIsBanned(true));
        stats.setDeletedUsers(userRepository.countByIsDeleted(true));
        // Time-based user statistics
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        stats.setNewUsersToday(userRepository.countUsersCreatedAfter(today));
        stats.setNewUsersThisWeek(userRepository.countUsersCreatedAfter(weekAgo));
        stats.setNewUsersThisMonth(userRepository.countUsersCreatedAfter(monthAgo));
        // Post statistics
        stats.setTotalPosts(postRepository.count());
        stats.setPostsToday(postRepository.countByCreatedAtAfter(today));
        stats.setPostsThisWeek(postRepository.countByCreatedAtAfter(weekAgo));
        stats.setPostsThisMonth(postRepository.countByCreatedAtAfter(monthAgo));
        // Report statistics
        stats.setPendingReports(reportRepository.countByStatus(com.revhub.model.Report.ReportStatus.PENDING));
        stats.setResolvedReports(reportRepository.countByStatus(com.revhub.model.Report.ReportStatus.RESOLVED));
        return stats;
    }
}
