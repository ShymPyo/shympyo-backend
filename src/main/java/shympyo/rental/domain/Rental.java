package shympyo.rental.domain;

import shympyo.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Rental {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "due_time", nullable = false)
    private LocalDateTime dueTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20) // ← 16 → 20
    private RentalStatus status;

    @Builder
    private Rental(Place place, User user, LocalDateTime startTime, LocalDateTime endTime,
                   LocalDateTime dueTime, RentalStatus status) {
        this.place = place;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dueTime = dueTime;
        this.status = (status == null ? RentalStatus.USING : status);

        if (this.place == null || this.user == null || this.startTime == null || this.dueTime == null) {
            throw new IllegalArgumentException("place/user/startTime/dueTime는 필수입니다.");
        }
    }

    public static Rental start(Place place, User user, LocalDateTime now) {
        int maxMinutes = place.getMaxUsageMinutes();
        LocalDateTime due = now.plusMinutes(maxMinutes);

        return Rental.builder()
                .place(place)
                .user(user)
                .startTime(now)
                .dueTime(due)
                .status(RentalStatus.USING)
                .build();
    }

    public void markTimeExceeded() {
        if (this.status == RentalStatus.USING) {
            this.status = RentalStatus.TIME_EXCEEDED;
        }
    }

    public void end(LocalDateTime endAt) {
        if (!isOngoing()) return;
        this.endTime = endAt;
        this.status = RentalStatus.ENDED;
    }

    public void cancel(LocalDateTime endAt) {
        if (!isOngoing()) return;
        this.endTime = endAt;
        this.status = RentalStatus.CANCELED;
    }

    public void kick(LocalDateTime endAt) {
        if (!isOngoing()) return;
        this.endTime = endAt;
        this.status = RentalStatus.KICKED;
    }

    public boolean isOngoing() {
        return this.status == RentalStatus.USING || this.status == RentalStatus.TIME_EXCEEDED;
    }

    public boolean isCanceled() { return this.status == RentalStatus.CANCELED; }
    public boolean isEnded()    { return this.status == RentalStatus.ENDED; }
    public boolean isKicked()   { return this.status == RentalStatus.KICKED; }
}

