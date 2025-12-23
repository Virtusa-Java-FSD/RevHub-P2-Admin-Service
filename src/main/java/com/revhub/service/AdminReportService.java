package com.revhub.service;
import com.revhub.model.Report;
import com.revhub.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final ReportRepository reportRepository;
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }
    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    public List<Report> getPendingReports() {
        return getReportsByStatus(Report.ReportStatus.PENDING);
    }
    public Report getReportById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }
    @Transactional
    public Report resolveReport(String reportId, Long adminId, String resolutionNote) {
        Report report = getReportById(reportId);
        report.setStatus(Report.ReportStatus.RESOLVED);
        report.setResolvedBy(adminId);
        report.setResolvedAt(LocalDateTime.now());
        report.setResolutionNote(resolutionNote);
        return reportRepository.save(report);
    }
    @Transactional
    public Report dismissReport(String reportId, Long adminId, String resolutionNote) {
        Report report = getReportById(reportId);
        report.setStatus(Report.ReportStatus.DISMISSED);
        report.setResolvedBy(adminId);
        report.setResolvedAt(LocalDateTime.now());
        report.setResolutionNote(resolutionNote);
        return reportRepository.save(report);
    }
    @Transactional
    public Report createReport(Report report) {
        report.setStatus(Report.ReportStatus.PENDING);
        return reportRepository.save(report);
    }
    public long countPendingReports() {
        return reportRepository.countByStatus(Report.ReportStatus.PENDING);
    }
}
