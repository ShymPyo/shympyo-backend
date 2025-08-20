package account_service.rental.repository;

import account_service.rental.domain.Place;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Place p where p.code = :code")
    Optional<Place> findByCodeForUpdate(@Param("code") String code);

}
