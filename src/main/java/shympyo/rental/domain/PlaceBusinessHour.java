package shympyo.rental.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PlaceBusinessHour {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="place_id", nullable = false)
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10) // MONDAY ~ SUNDAY
    private DayOfWeek dayOfWeek;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;

    @Column(name = "closed", nullable = false)
    private boolean closed;

    public void update(LocalTime open, LocalTime close,
                       LocalTime bStart, LocalTime bEnd, boolean closed) {
        this.openTime = open;
        this.closeTime = close;
        this.breakStart = bStart;
        this.breakEnd = bEnd;
        this.closed = closed;
    }

}
