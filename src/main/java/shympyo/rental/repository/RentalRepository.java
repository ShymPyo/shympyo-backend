package shympyo.rental.repository;

import org.hibernate.query.Page;
import shympyo.rental.domain.Rental;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shympyo.rental.dto.CurrentRentalResponse;
import shympyo.rental.dto.RentalHistoryResponse;
import shympyo.rental.dto.RentalResponse;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByPlaceIdAndStatus(Long placeId, String status);

    @Query("""
        select case when count(r) > 0 then true else false end
        from Rental r
        where r.user.id = :userId
          and r.place.id = :placeId
          and r.status = 'ended'
    """)
    boolean hasEndedRental(@Param("userId") Long userId,
                           @Param("placeId") Long placeId);

    @Query("select r from Rental r where r.user.id = :userId and r.status = :status")
    List<Rental> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rental r where r.id = :id")
    Optional<Rental> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        select new shympyo.rental.dto.CurrentRentalResponse(
            r.id, u.id, u.name, u.imageUrl, r.startTime
        )
        from Rental r
        join r.user u
        join r.place p
        where p.id = :placeId
          and r.startTime <= CURRENT_TIMESTAMP
          and (r.endTime is null or r.endTime >= CURRENT_TIMESTAMP)
        order by r.startTime desc
    """)
    List<CurrentRentalResponse> findCurrentRentalsWithUserByPlace(@Param("placeId") Long placeId);

    @Query("""
        select new shympyo.rental.dto.RentalHistoryResponse(
            r.id,
            u.id,
            u.name,
            r.startTime,
            r.endTime,
            r.status
        )
        from Rental r
          join r.user u
          join r.place p
        where p.id = :placeId
        order by r.startTime desc
    """)
    List<RentalHistoryResponse> findAllHistoryByPlace(@Param("placeId") Long placeId);
}
