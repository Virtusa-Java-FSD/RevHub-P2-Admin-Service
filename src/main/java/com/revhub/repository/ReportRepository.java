package com.revhub.repository;
import com.revhub.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByStatus(Report.ReportStatus status);
    List<Report> findByType(Report.ReportType type);
    List<Report> findByReporterId(Long reporterId);
    List<Report> findByReportedEntityId(String reportedEntityId);
    List<Report> findByReportedEntityType(String reportedEntityType);
    long countByStatus(Report.ReportStatus status);
    List<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status);
    List<Report> findAllByOrderByCreatedAtDesc();
}
