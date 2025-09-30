package shympyo.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shympyo.rental.domain.PlaceBusinessHour;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceBusinessHourRepository extends JpaRepository<PlaceBusinessHour, Long> {

    List<PlaceBusinessHour> findByPlaceId(Long placeId);

    boolean existsByPlaceId(Long placeId);

    Optional<PlaceBusinessHour> findByPlaceIdAndDayOfWeek(Long placeId, DayOfWeek day);


}
