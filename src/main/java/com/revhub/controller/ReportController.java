package com.revhub.controller;
import com.revhub.dto.CreateReportRequest;
import com.revhub.model.Report;
import com.revhub.model.User;
import com.revhub.service.AdminReportService;
import com.revhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {
    private final AdminReportService adminReportService;
    private final UserService userService;
    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody CreateReportRequest request,
            Authentication authentication) {
        User reporter = userService.getCurrentUser(authentication);
        Report report = new Report();
        report.setReporterId(reporter.getId());
        report.setReporterName(reporter.getUsername());
        report.setReportedEntityId(request.getReportedEntityId());
        report.setReportedEntityType(request.getReportedEntityType());
        report.setType(request.getType());
        report.setDescription(request.getDescription());
        report.setReason(request.getType().name());
        report.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(adminReportService.createReport(report));
    }
}
