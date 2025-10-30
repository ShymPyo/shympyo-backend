package shympyo.report.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shympyo.user.domain.User;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(
        name = "sanction",
        indexes = {
                @Index(name = "idx_sanction_target_user", columnList = "target_user_id"),
                @Index(name = "idx_sanction_status", columnList = "status"),
                @Index(name = "idx_sanction_period", columnList = "startAt,endAt")
        }
)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Sanction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 제재 대상 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    /** 제재 유형(경고/정지/차단 등) */
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SanctionType type;

    /** 제재 범위: 전체/특정 제공자/특정 장소 */
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SanctionScope scope;

    /** scope 보조키: PROVIDER_ONLY/PLACE_ONLY일 때 참조 ID(예: providerId, placeId) */
    private Long scopeRefId;

    /** PLACE_CATEGORY 등 enum 카테고리용 */
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PlaceType scopeRefCategory;

    /** 사유(카테고리) */
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SanctionReason reason;

    /** 상세 사유(운영 메모/근거 요약) */
    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    /** 진행 상태 */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private SanctionStatus status = SanctionStatus.ACTIVE;

    /** 생성 주체: MANUAL(관리자)/AUTO(임계치 자동) */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private SanctionSource source;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /* ===== 라이프사이클 ===== */
    @PrePersist
    void onCreate() {
        if (this.startAt == null) this.startAt = LocalDateTime.now();
    }

    /* ===== 도메인 로직 ===== */
    public boolean isActiveAt(LocalDateTime now) {
        if (this.status != SanctionStatus.ACTIVE) return false;
        boolean started = !now.isBefore(this.startAt);
        boolean notEnded = (this.endAt == null) || now.isBefore(this.endAt);
        return started && notEnded;
    }

    public void revoke(String note) {
        this.status = SanctionStatus.REVOKED;
        if (note != null && !note.isBlank()) {
            this.detail = (this.detail == null ? "" : this.detail + "\n") + "[REVOKE] " + note;
        }
    }

    public void expireNow() {
        this.status = SanctionStatus.EXPIRED;
        this.endAt = LocalDateTime.now();
    }

    public void extendUntil(LocalDateTime newEnd, String note) {
        if (this.status != SanctionStatus.ACTIVE) return;
        if (this.endAt == null || newEnd == null || newEnd.isAfter(this.endAt)) {
            this.endAt = newEnd;
        }
        if (note != null && !note.isBlank()) {
            this.detail = (this.detail == null || this.detail.isBlank())
                    ? note
                    : this.detail + "\n" + note;
        }
    }
}
