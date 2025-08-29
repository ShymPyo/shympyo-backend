package shympyo.letter.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shympyo.rental.domain.Place;
import shympyo.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Letter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false, length = 2000)
    private String content;

    /** 읽음 여부 + 읽은 시각 */
    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 읽음 처리 */
    public void markAsRead(LocalDateTime now) {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = now;
        }
    }
}
