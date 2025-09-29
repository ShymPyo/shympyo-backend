package shympyo.map.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shympyo.map.domain.Map;

import java.util.List;

@Repository
public interface MapRepository extends JpaRepository<Map, Long> {


    @Query("""
        SELECT m FROM Map m
        WHERE m.latitude BETWEEN :minLat AND :maxLat
          AND m.longitude BETWEEN :minLon AND :maxLon
        """)
    List<Map> findInBoundingBox(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon
    );

    @Query("""
        SELECT m
        FROM Map m
        WHERE m.latitude  BETWEEN :minLat AND :maxLat
          AND m.longitude BETWEEN :minLon AND :maxLon
          AND m.type IN :types
    """)
    List<shympyo.map.domain.Map> findInBoundingBox(
            double minLat, double maxLat,
            double minLon, double maxLon,
            List<shympyo.map.domain.PlaceType> types
    );
}
