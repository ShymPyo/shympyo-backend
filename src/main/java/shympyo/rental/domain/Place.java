package shympyo.rental.domain;

import jakarta.persistence.*;
import lombok.Getter;
import shympyo.user.domain.User;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 장소 이름 */
    @Column(length = 100, nullable = false)
    private String name;

    /** 설명 */
    @Lob
    private String content;

    /** 최대 동시 수용 인원 */
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    /** 위도/경도 (WGS84) */
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /** 주소 */
    @Column(length = 200)
    private String address;

    /** 영업 시작/종료 시간 (일자와 무관한 반복 스케줄) */
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    /** 정기 휴무일(예: SUNDAY). 여러 요일이 필요하면 추후 컬렉션으로 분리 */
    @Enumerated(EnumType.STRING)
    @Column(name = "weekly_holiday")
    private DayOfWeek weeklyHoliday;

    /** 장소 고유 코드 (QR에 넣을 고정 식별자) */
    @Column(nullable = false, length = 50)
    private String code;

    /** 소유자/관리자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_place_owner"))
    private User owner;

}
