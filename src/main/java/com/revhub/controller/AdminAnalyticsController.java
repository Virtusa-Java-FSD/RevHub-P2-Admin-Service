package com.revhub.controller;
import com.revhub.dto.AdminStatsResponse;
import com.revhub.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// @PreAuthorize("hasAuthority('ADMIN')") // Temporarily disabled for testing
public class AdminAnalyticsController {
    private final AdminAnalyticsService adminAnalyticsService;
    @GetMapping("/overview")
    public ResponseEntity<AdminStatsResponse> getOverviewStats() {
        AdminStatsResponse stats = adminAnalyticsService.getOverviewStats();
        return ResponseEntity.ok(stats);
    }
}
