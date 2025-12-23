package com.revhub.service;
import com.revhub.exception.ResourceNotFoundException;
import com.revhub.model.Report;
import com.revhub.model.User;
import com.revhub.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    public Report createReport(Report report) {
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus(Report.ReportStatus.PENDING);
        report.setActionTaken(Report.ActionTaken.NONE);
        return reportRepository.save(report);
    }
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    public Report getReportById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }
    public Report resolveReport(String reportId, Authentication authentication, String resolutionNote) {
        User admin = userService.getCurrentUser(authentication);
        Report report = getReportById(reportId);
        report.setStatus(Report.ReportStatus.RESOLVED);
        report.setResolvedBy(admin.getId());
        report.setResolvedAt(LocalDateTime.now());
        report.setResolutionNote(resolutionNote);
        return reportRepository.save(report);
    }
    public Report updateReportAction(String reportId, Report.ActionTaken actionTaken) {
        Report report = getReportById(reportId);
        report.setActionTaken(actionTaken);
        report.setStatus(Report.ReportStatus.RESOLVED);
        return reportRepository.save(report);
    }
    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }
}
