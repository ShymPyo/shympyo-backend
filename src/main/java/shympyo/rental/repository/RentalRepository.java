package shympyo.rental.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shympyo.rental.domain.Rental;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shympyo.rental.dto.PlaceCurrentRentalResponse;
import shympyo.rental.dto.PlaceRentalHistoryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    boolean existsByIdAndPlaceOwnerId(Long rentalId, Long ownerId);
    long countByPlaceIdAndStatus(Long placeId, String status);
    boolean existsByUserIdAndStatus(Long userId, String status);
    Optional<Rental> findByIdAndUserId(Long rentalId, Long userId);

    @Query("select r from Rental r where r.user.id = :userId and r.status = :status")
    List<Rental> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rental r where r.id = :id")
    Optional<Rental> findByIdForUpdate(@Param("id") Long id);


    @Query("""
        select case when count(r) > 0 then true else false end
        from Rental r
        where r.user.id = :userId
          and r.place.id = :placeId
          and r.status = 'ended'
    """)
    boolean hasEndedRental(@Param("userId") Long userId,
                           @Param("placeId") Long placeId);



    @Query("""
        SELECT r
        FROM Rental r
        WHERE r.user.id = :userId
          AND r.status = :status
        ORDER BY r.endTime DESC, r.id DESC
    """)
        Slice<Rental> findEndedByUser(@Param("userId") Long userId,
                                      @Param("status") String status,
                                      Pageable pageable);

    @Query("""
        SELECT r
        FROM Rental r
        WHERE r.user.id = :userId
          AND r.status = :status
          AND ( r.endTime < :cursorEndTime
             OR (r.endTime = :cursorEndTime AND r.id < :cursorId) )
        ORDER BY r.endTime DESC, r.id DESC
    """)
    Slice<Rental> findEndedByUserWithCursor(@Param("userId") Long userId,
                                            @Param("status") String status,
                                            @Param("cursorEndTime") LocalDateTime cursorEndTime,
                                            @Param("cursorId") Long cursorId,
                                            Pageable pageable);

    @Query("""
        select new shympyo.rental.dto.PlaceCurrentRentalResponse(
            r.id, u.id, u.nickname, u.bio, u.imageUrl, r.startTime
        )
        from Rental r
        join r.user u
        join r.place p
        where p.id = :placeId
          and r.startTime <= CURRENT_TIMESTAMP
          and (r.endTime is null or r.endTime >= CURRENT_TIMESTAMP)
        order by r.startTime desc
    """)
    List<PlaceCurrentRentalResponse> findCurrentRentalsWithUserByPlace(@Param("placeId") Long placeId);


    @Query("""
        select new shympyo.rental.dto.PlaceRentalHistoryResponse(
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
    List<PlaceRentalHistoryResponse> findAllHistoryByPlace(@Param("placeId") Long placeId);
}
