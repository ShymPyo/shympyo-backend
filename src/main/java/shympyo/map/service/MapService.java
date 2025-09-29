package shympyo.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shympyo.map.domain.Map;
import shympyo.map.domain.PlaceType;
import shympyo.map.dto.NearbyListResponse;
import shympyo.map.dto.NearbyMapResponse;
import shympyo.map.dto.PlaceDetailResponse;
import shympyo.map.repository.MapRepository;
import shympyo.rental.repository.PlaceRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private final PlaceRepository placeRepository;

    private static final double EARTH_R = 6371000.0;

    private static final List<PlaceType> DEFAULT_TYPES = List.copyOf(EnumSet.allOf(PlaceType.class));

    private static class BoundingBox {
        final double minLat;
        final double maxLat;
        final double minLon;
        final double maxLon;

        BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }
    }

    public List<NearbyMapResponse> findNearby(double lat, double lon, int radiusMeters, int limit) {
        return findNearby(lat, lon, radiusMeters, limit, DEFAULT_TYPES);
    }

    public List<NearbyMapResponse> findNearby(double lat, double lon, int radiusMeters, int limit, Collection<PlaceType> includeTypes){

        final int radius   = Math.max(1, Math.min(radiusMeters, 5_000)); // 1m ~ 5km
        final int maxLimit = Math.max(1, Math.min(limit, 200));

        BoundingBox box = calculateBoundingBox(lat, lon, radius);

        var types = (includeTypes == null || includeTypes.isEmpty())
                ? DEFAULT_TYPES
                : List.copyOf(includeTypes);

        List<shympyo.map.domain.Map> candidates =
                mapRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon, includeTypes);

        record Cand(shympyo.map.domain.Map m, double dist) {}

        return candidates.stream()
                .map(m -> new Cand(m, haversine(lat, lon, m.getLatitude(), m.getLongitude())))
                .filter(c -> c.dist() <= radius)
                .sorted(Comparator.comparingDouble(Cand::dist))
                .limit(maxLimit)
                .map(c -> {
                    var m = c.m();
                    return new NearbyMapResponse(
                            m.getId(),
                            m.getLatitude(),
                            m.getLongitude(),
                            m.getType()
                    );
                })
                .toList();
    }

    public List<NearbyListResponse> findNearbyList(double lat, double lon, int radiusMeters, int limit) {

        final int radius   = Math.max(1, Math.min(radiusMeters, 5_000));
        final int maxLimit = Math.max(1, Math.min(limit, 200));

        final double latDeg = radius / 111_320.0;
        double cosLat = Math.cos(Math.toRadians(lat));
        if (Math.abs(cosLat) < 1e-12) cosLat = 1e-12;
        final double lonDeg = radius / (111_320.0 * cosLat);

        final double minLat = lat - latDeg, maxLat = lat + latDeg;
        final double minLon = lon - lonDeg, maxLon = lon + lonDeg;

        List<shympyo.map.domain.Map> candidates =
                mapRepository.findInBoundingBox(minLat, maxLat, minLon, maxLon);

        record Cand(shympyo.map.domain.Map m, double dist) {}

        return candidates.stream()
                .map(m -> new Cand(m, haversine(lat, lon, m.getLatitude(), m.getLongitude())))
                .filter(c -> c.dist() <= radius)
                .sorted(Comparator.comparingDouble(Cand::dist))
                .limit(maxLimit)
                .map(c -> {
                    var m = c.m();
                    return new NearbyListResponse(
                            m.getId(),
                            m.getName(),
                            m.getAddress(),
                            m.getContent(),    // LOB이면 길 수 있음
                            m.getType(),
                            c.dist()
                    );
                })
                .toList();
    }

    public PlaceDetailResponse findPlace(Long placeId){

        Map map = mapRepository.findById(placeId)
                .orElseThrow(()-> new IllegalArgumentException("해당 장소가 없습니다."));

        return PlaceDetailResponse.builder()
                .id(map.getId())
                .name(map.getName())
                .type(map.getType())
                .longitude(map.getLongitude())
                .address(map.getAddress())
                .latitude(map.getLatitude())
                .build();
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_R * Math.asin(Math.sqrt(a));
    }

    private BoundingBox calculateBoundingBox(double lat, double lon, int radiusMeters) {
        final int radius = Math.max(1, Math.min(radiusMeters, 5_000)); // 1m ~ 5km

        final double latDeg = radius / 111_320.0; // 위도 1도 ≈ 111.32km
        double cosLat = Math.cos(Math.toRadians(lat));
        if (Math.abs(cosLat) < 1e-12) cosLat = 1e-12; // 극지방 분모 0 방지
        final double lonDeg = radius / (111_320.0 * cosLat);

        return new BoundingBox(
                lat - latDeg, lat + latDeg,
                lon - lonDeg, lon + lonDeg
        );
    }
}