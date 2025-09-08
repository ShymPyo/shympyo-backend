package shympyo.map.service;

import lombok.RequiredArgsConstructor;
import shympyo.map.domain.Map;
import org.springframework.stereotype.Service;
import shympyo.map.dto.NearbyMapDto;
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
    public List<NearbyMapDto> findNearby(double lat, double lon, int radiusMeters, int limit){

        // 안전 범위 설정
        int radius = Math.max(1, Math.min(radiusMeters, 5000));
        int maxLimit = Math.max(1, Math.min(limit, 200));

        // 위도/경도 → degree 변환 값
        double latDeg = radius / 111_320.0; // 위도 1도 ≈ 111.32km
        double lonDeg = radius / (111_320.0 * Math.cos(Math.toRadians(lat)));

        double minLat = lat - latDeg;
        double maxLat = lat + latDeg;
        double minLon = lon - lonDeg;
        double maxLon = lon + lonDeg;

        // 후보 조회
        List<Map> candidates = mapRepository.findInBoundingBox(minLat, maxLat, minLon, maxLon);

        // 거리 계산 + 필터링 + 정렬 + 제한
        return candidates.stream()
                .map(m -> {
                    double d = haversine(lat, lon, m.getLatitude(), m.getLongitude());
                    return new NearbyMapDto(
                            m.getId(), m.getName(), m.getAddress(), m.getType(),
                            m.getLatitude(), m.getLongitude(), d
                    );
                })
                .filter(dto -> dto.getDistanceM() <= radius)
                .sorted(Comparator.comparingDouble(NearbyMapDto::getDistanceM))
                .limit(maxLimit)
                .toList();
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