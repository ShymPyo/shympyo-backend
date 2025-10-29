package shympyo.rental.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shympyo.rental.domain.QRental;
import shympyo.rental.domain.RentalStatus;
import shympyo.rental.dto.PlaceRentalHistoryResponse;
import shympyo.user.domain.QUser;

import static shympyo.rental.domain.QRental.rental;

@Repository
@RequiredArgsConstructor
public class RentalQueryRepository {

    private final JPAQueryFactory queryFactory;

    public long countUsingByPlace(Long placeId, RentalStatus status) {
        return queryFactory
                .select(rental.id.count())
                .from(rental)
                .where(
                        rental.place.id.eq(placeId),
                        rental.status.eq(status)
                )
                .fetchOne();
    }

    public boolean existsUserRentalWithStatus(Long userId, RentalStatus status) {
        Long foundId = queryFactory
                .select(rental.id)
                .from(rental)
                .where(
                        rental.user.id.eq(userId),
                        rental.status.eq(status)
                )
                .limit(1)
                .fetchOne();

        return foundId != null;
    }

    public List<PlaceRentalHistoryResponse> findAllHistoryByPlace(Long placeId) {
        QRental r = QRental.rental;
        QUser u = QUser.user;

        return queryFactory
                .select(Projections.constructor(
                        PlaceRentalHistoryResponse.class,
                        r.id,         // rentalId          Long
                        u.id,         // userId            Long
                        u.name,       // userName          String
                        r.startTime,  // startTime         LocalDateTime
                        r.endTime,    // endTime           LocalDateTime
                        r.status      // status            RentalStatus (enum)
                ))
                .from(r)
                .join(r.user, u)
                .where(r.place.id.eq(placeId))
                .orderBy(r.startTime.desc(), r.id.desc())
                .fetch();
    }

}
