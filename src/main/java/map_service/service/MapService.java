package map_service.service;

import map_service.domain.Map;
import map_service.dto.MapResponse;
import map_service.dto.MapDistanceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MapService {
    private final List<Map> maps = new ArrayList<>();

    public MapService() {
        maps.add(Map.builder()
                .mapId(1)
                .name("지도1")
                .description("설명1")
                .longitude(127.0f)
                .latitude(37.5f)
                .address("서울시")
                .type("typeA")
                .build());
        maps.add(Map.builder()
                .mapId(2)
                .name("지도2")
                .description("설명2")
                .longitude(128.0f)
                .latitude(36.5f)
                .address("부산시")
                .type("typeB")
                .build());
    }

    public List<MapResponse> getAllMaps() {
        List<MapResponse> result = new ArrayList<>();
        for (Map map : maps) {
            result.add(MapResponse.builder()
                    .mapId(map.getMapId())
                    .name(map.getName())
                    .description(map.getDescription())
                    .longitude(map.getLongitude())
                    .latitude(map.getLatitude())
                    .address(map.getAddress())
                    .type(map.getType())
                    .build());
        }
        return result;
    }

    public List<MapDistanceResponse> getDistances(double userLat, double userLng) {
        List<MapDistanceResponse> result = new ArrayList<>();
        for (Map map : maps) {
            double distance = calculateDistance(userLat, userLng, map.getLatitude(), map.getLongitude());
            result.add(MapDistanceResponse.builder()
                    .mapId(map.getMapId())
                    .name(map.getName())
                    .distance(distance)
                    .build());
        }
        return result;
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}