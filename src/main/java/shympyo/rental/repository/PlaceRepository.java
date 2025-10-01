package shympyo.rental.repository;

import shympyo.rental.domain.Place;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Place p where p.code = :code")
    Optional<Place> findByCodeForUpdate(@Param("code") String code);

    Optional<Place> findByOwnerId(Long ownerId);


    @Query("""
        SELECT p
        FROM Place p
        WHERE p.latitude  BETWEEN :minLat AND :maxLat
          AND p.longitude BETWEEN :minLon AND :maxLon
          AND p.status = 'ACTIVE'
    """)
    List<Place> findInBoundingBox(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon
    );

    boolean existsByCode(String code);


}
