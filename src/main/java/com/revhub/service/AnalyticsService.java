package com.revhub.service;

import com.revhub.model.Post;
import com.revhub.model.User;
import com.revhub.repository.CommentRepository;
import com.revhub.repository.LikeRepository;
import com.revhub.repository.PostRepository;
import com.revhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

        private final UserRepository userRepository;
        private final PostRepository postRepository;
        private final LikeRepository likeRepository;
        private final CommentRepository commentRepository;

        // User Growth Data (Last 7 months)
        public List<Map<String, Object>> getUserGrowthData() {
                List<Map<String, Object>> growthData = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();

                for (int i = 6; i >= 0; i--) {
                        LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0)
                                        .withSecond(0);
                        LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

                        long userCount = userRepository.findAll().stream()
                                        .filter(user -> user.getCreatedAt() != null &&
                                                        !user.getCreatedAt().isAfter(monthEnd))
                                        .count();

                        Map<String, Object> monthData = new HashMap<>();
                        monthData.put("month", monthStart.format(DateTimeFormatter.ofPattern("MMM")));
                        monthData.put("users", userCount);
                        growthData.add(monthData);
                }

                return growthData;
        }

        // Post Activity Data (Last 7 days)
        public List<Map<String, Object>> getPostActivityData() {
                List<Map<String, Object>> activityData = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

                for (int i = 6; i >= 0; i--) {
                        LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
                        LocalDateTime dayEnd = dayStart.withHour(23).withMinute(59).withSecond(59);

                        long postCount = postRepository.findAll().stream()
                                        .filter(post -> post.getCreatedAt() != null &&
                                                        !post.getCreatedAt().isBefore(dayStart) &&
                                                        !post.getCreatedAt().isAfter(dayEnd))
                                        .count();

                        Map<String, Object> dayData = new HashMap<>();
                        dayData.put("day", days[dayStart.getDayOfWeek().getValue() % 7]);
                        dayData.put("posts", postCount);
                        activityData.add(dayData);
                }

                return activityData;
        }

        // User Distribution Data
        public Map<String, Object> getUserDistributionData() {
                List<User> allUsers = userRepository.findAll();

                long activeUsers = allUsers.stream()
                                .filter(user -> !Boolean.TRUE.equals(user.getIsBanned())
                                                && !Boolean.TRUE.equals(user.getIsDeleted()))
                                .count();

                long bannedUsers = allUsers.stream()
                                .filter(user -> Boolean.TRUE.equals(user.getIsBanned()))
                                .count();

                long inactiveUsers = allUsers.size() - activeUsers - bannedUsers;

                List<Map<String, Object>> distribution = new ArrayList<>();

                Map<String, Object> active = new HashMap<>();
                active.put("name", "Active");
                active.put("value", activeUsers);
                active.put("color", "#10b981");
                distribution.add(active);

                Map<String, Object> inactive = new HashMap<>();
                inactive.put("name", "Inactive");
                inactive.put("value", inactiveUsers);
                inactive.put("color", "#6b7280");
                distribution.add(inactive);

                Map<String, Object> banned = new HashMap<>();
                banned.put("name", "Banned");
                banned.put("value", bannedUsers);
                banned.put("color", "#ef4444");
                distribution.add(banned);

                Map<String, Object> result = new HashMap<>();
                result.put("distribution", distribution);
                return result;
        }

        // Engagement Trends Data (Last 7 days)
        public List<Map<String, Object>> getEngagementTrendsData() {
                List<Map<String, Object>> trendsData = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();

                for (int i = 6; i >= 0; i--) {
                        LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
                        LocalDateTime dayEnd = dayStart.withHour(23).withMinute(59).withSecond(59);

                        // Get posts for this day
                        List<Post> dayPosts = postRepository.findAll().stream()
                                        .filter(post -> post.getCreatedAt() != null &&
                                                        !post.getCreatedAt().isBefore(dayStart) &&
                                                        !post.getCreatedAt().isAfter(dayEnd))
                                        .collect(Collectors.toList());

                        // Count likes for these posts
                        long likesCount = dayPosts.stream()
                                        .mapToLong(Post::getLikesCount)
                                        .sum();

                        // Count comments for these posts
                        long commentsCount = dayPosts.stream()
                                        .mapToLong(Post::getCommentsCount)
                                        .sum();

                        // Shares (placeholder - you can implement if you have shares)
                        long sharesCount = dayPosts.stream()
                                        .mapToLong(post -> post.getSharesCount() != null ? post.getSharesCount() : 0)
                                        .sum();

                        Map<String, Object> dayData = new HashMap<>();
                        dayData.put("date", dayStart.format(DateTimeFormatter.ofPattern("MMM dd")));
                        dayData.put("likes", likesCount);
                        dayData.put("comments", commentsCount);
                        dayData.put("shares", sharesCount);
                        trendsData.add(dayData);
                }

                return trendsData;
        }

        // Get all analytics data at once
        public Map<String, Object> getAllAnalyticsData() {
                Map<String, Object> analytics = new HashMap<>();
                analytics.put("userGrowth", getUserGrowthData());
                analytics.put("postActivity", getPostActivityData());
                analytics.put("userDistribution", getUserDistributionData());
                analytics.put("engagementTrends", getEngagementTrendsData());
                return analytics;
        }
}
