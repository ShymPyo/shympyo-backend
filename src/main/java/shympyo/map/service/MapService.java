package shympyo.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shympyo.map.domain.Map;
import shympyo.map.dto.NearbyListResponse;
import shympyo.map.dto.NearbyMapResponse;
import shympyo.map.dto.PlaceDetailResponse;
import shympyo.map.repository.MapRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private static final double EARTH_R = 6371000.0; // 지구 반지름(m)


    // 조회하기
    // 현재 좌표를 기준으로 반경 몇 미터 안에 장소를 반환
    public List<NearbyMapResponse> findNearby(double lat, double lon, int radiusMeters, int limit) {
        // 1) 파라미터 가드
        final int radius   = Math.max(1, Math.min(radiusMeters, 5_000)); // 1m ~ 5km
        final int maxLimit = Math.max(1, Math.min(limit, 200));

        // 2) 바운딩 박스(deg)
        final double latDeg = radius / 111_320.0; // 위도 1도 ≈ 111.32km
        double cosLat = Math.cos(Math.toRadians(lat));
        if (Math.abs(cosLat) < 1e-12) cosLat = 1e-12; // 극지방 분모 0 방지
        final double lonDeg = radius / (111_320.0 * cosLat);

        final double minLat = lat - latDeg, maxLat = lat + latDeg;
        final double minLon = lon - lonDeg, maxLon = lon + lonDeg;

        // 3) 후보 조회 (인덱스 권장: CREATE INDEX IF NOT EXISTS map_lat_lon_idx ON map(latitude, longitude))
        List<shympyo.map.domain.Map> candidates =
                mapRepository.findInBoundingBox(minLat, maxLat, minLon, maxLon);

        // 4) 거리 계산(중간 보관) → 반경 필터 → 거리 오름차순 정렬 → DTO로 투영 → 제한
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
        // 파라미터 가드
        final int radius   = Math.max(1, Math.min(radiusMeters, 5_000)); // 1m ~ 5km
        final int maxLimit = Math.max(1, Math.min(limit, 200));

        // 바운딩 박스(degree)
        final double latDeg = radius / 111_320.0; // 위도 1도 ≈ 111.32km
        double cosLat = Math.cos(Math.toRadians(lat));
        if (Math.abs(cosLat) < 1e-12) cosLat = 1e-12; // 극지방 분모 0 방지
        final double lonDeg = radius / (111_320.0 * cosLat);

        final double minLat = lat - latDeg, maxLat = lat + latDeg;
        final double minLon = lon - lonDeg, maxLon = lon + lonDeg;

        // 후보 조회 (인덱스 권장: CREATE INDEX IF NOT EXISTS map_lat_lon_idx ON map(latitude, longitude))
        List<shympyo.map.domain.Map> candidates =
                mapRepository.findInBoundingBox(minLat, maxLat, minLon, maxLon);

        // 거리 계산 → 반경 필터 → 거리 정렬 → DTO 매핑 → 제한
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

    // 하버사인 공식
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_R * Math.asin(Math.sqrt(a));
    }
}