package shympyo.rental.repository;

import shympyo.rental.domain.Rental;
import account_service.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByPlaceIdAndStatus(Long placeId, String status);


    @Query("select r from Rental r where r.user.id = :userId and r.status = :status")
    List<Rental> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rental r where r.id = :id")
    Optional<Rental> findByIdForUpdate(@Param("id") Long id);

}
