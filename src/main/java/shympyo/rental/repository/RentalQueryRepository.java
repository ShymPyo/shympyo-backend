package shympyo.rental.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shympyo.rental.domain.RentalStatus;

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
}
