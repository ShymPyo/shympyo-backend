package shympyo.letter.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import shympyo.rental.domain.Rental;
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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false, unique = true)
    private Rental rental;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void markAsRead(LocalDateTime now) {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = now;
        }
    }
}
