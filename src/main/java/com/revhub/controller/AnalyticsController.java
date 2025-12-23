package com.revhub.controller;

import com.revhub.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/user-growth")
    public ResponseEntity<List<Map<String, Object>>> getUserGrowth() {
        return ResponseEntity.ok(analyticsService.getUserGrowthData());
    }

    @GetMapping("/post-activity")
    public ResponseEntity<List<Map<String, Object>>> getPostActivity() {
        return ResponseEntity.ok(analyticsService.getPostActivityData());
    }

    @GetMapping("/user-distribution")
    public ResponseEntity<Map<String, Object>> getUserDistribution() {
        return ResponseEntity.ok(analyticsService.getUserDistributionData());
    }

    @GetMapping("/engagement-trends")
    public ResponseEntity<List<Map<String, Object>>> getEngagementTrends() {
        return ResponseEntity.ok(analyticsService.getEngagementTrendsData());
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllAnalytics() {
        return ResponseEntity.ok(analyticsService.getAllAnalyticsData());
    }
}
