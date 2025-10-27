package shympyo.rental.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import static shympyo.rental.domain.QPlaceBusinessHour.placeBusinessHour;

@Repository
@RequiredArgsConstructor
public class PlaceBusinessHourQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean isOpenNow(Long placeId, DayOfWeek dayOfWeek, LocalTime nowTime) {
        Long rowId = queryFactory
                .select(placeBusinessHour.id)
                .from(placeBusinessHour)
                .where(
                        placeBusinessHour.place.id.eq(placeId),
                        placeBusinessHour.dayOfWeek.eq(dayOfWeek),
                        placeBusinessHour.closed.isFalse(),

                        placeBusinessHour.openTime.isNotNull(),
                        placeBusinessHour.closeTime.isNotNull(),
                        placeBusinessHour.openTime.loe(nowTime),
                        placeBusinessHour.closeTime.gt(nowTime),

                        // 휴식 시간에 걸려있지 않은가?
                        placeBusinessHour.breakStart.isNull()
                                .or(placeBusinessHour.breakEnd.isNull())
                                .or(
                                        // nowTime not between breakStart and breakEnd
                                        // (breakStart > now or breakEnd <= now)
                                        placeBusinessHour.breakStart.gt(nowTime)
                                                .or(placeBusinessHour.breakEnd.loe(nowTime))
                                )
                )
                .limit(1)
                .fetchOne();

        return rowId != null;
    }
}
