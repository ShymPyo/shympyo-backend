package shympyo.rental.domain;

import account_service.user.domain.User;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false, length = 16)
    private String status; // using | ended | canceled

    @Builder
    private Rental(Place place, User user, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.place = place;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = (status == null ? "using" : status);
    }

    /** 이용 시작 헬퍼 */
    public static Rental start(Place place, User user, LocalDateTime now) {
        return Rental.builder()
                .place(place)
                .user(user)
                .startTime(now)
                .status("using")
                .build();
    }

    /** 이용 종료 헬퍼 */
    public void end(LocalDateTime endAt) {
        if (!"using".equals(this.status)) return;
        this.endTime = endAt;
        this.status = "ended";
    }

}
