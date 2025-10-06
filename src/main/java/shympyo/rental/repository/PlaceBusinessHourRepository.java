package shympyo.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shympyo.rental.domain.PlaceBusinessHour;



import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceBusinessHourRepository extends JpaRepository<PlaceBusinessHour, Long> {

    List<PlaceBusinessHour> findByPlaceId(Long placeId);

    boolean existsByPlaceId(Long placeId);

    Optional<PlaceBusinessHour> findByPlaceIdAndDayOfWeek(Long placeId, DayOfWeek day);

    List<PlaceBusinessHour> findByPlaceIdAndClosedTrue(Long placeId);

    @Query("""
    SELECT (COUNT(pbh) > 0)
    FROM PlaceBusinessHour pbh
    WHERE pbh.place.id = :placeId
      AND pbh.dayOfWeek = :today
      AND pbh.closed = false
      AND pbh.openTime IS NOT NULL
      AND pbh.closeTime IS NOT NULL
      AND :nowLocal >= pbh.openTime
      AND :nowLocal <  pbh.closeTime
      AND (
            (pbh.breakStart IS NULL OR pbh.breakEnd IS NULL)
         OR NOT (:nowLocal >= pbh.breakStart AND :nowLocal < pbh.breakEnd)
      )
    """)
    boolean existsOpenNow(
            @Param("placeId") Long placeId,
            @Param("today") DayOfWeek today,
            @Param("nowLocal") LocalTime nowLocal
    );


}
