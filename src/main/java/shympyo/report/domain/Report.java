package shympyo.report.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shympyo.user.domain.User;

import java.time.LocalDateTime;

@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Column(name = "rental_id")
    private Long rentalId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    @Builder.Default
    private ReportAction action = ReportAction.NONE;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    private LocalDateTime processedAt;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void resolve(ReportAction action, String adminNote) {
        this.status = ReportStatus.RESOLVED;
        this.action = action == null ? ReportAction.NONE : action;
        this.adminNote = adminNote;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(String adminNote) {
        this.status = ReportStatus.REJECTED;
        this.action = ReportAction.NONE;
        this.adminNote = adminNote;
        this.processedAt = LocalDateTime.now();
    }

}
