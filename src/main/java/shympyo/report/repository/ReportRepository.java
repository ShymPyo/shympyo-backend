package shympyo.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shympyo.report.domain.Report;
import shympyo.report.domain.ReportAction;
import shympyo.report.domain.ReportStatus;

import java.time.LocalDateTime;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByRentalIdAndReportedUserId(Long rentalId, Long reportedUserId);

    long countByReportedUserIdAndStatus(Long reportedUserId, ReportStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update Report r
       set r.status = :resolvedStatus,
           r.action = :action,
           r.adminNote = concat(coalesce(r.adminNote, ''), :note),
           r.processedAt = :now
     where r.reportedUser.id = :userId
       and r.status = :pendingStatus
    """)
    int resolveAllPendingByUser(Long userId,
                                ReportStatus pendingStatus,
                                ReportStatus resolvedStatus,
                                ReportAction action,
                                String note,
                                LocalDateTime now);
}
