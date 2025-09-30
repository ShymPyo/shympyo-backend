package shympyo.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shympyo.map.domain.Map;
import shympyo.map.domain.PlaceType;
import shympyo.map.dto.NearbyListResponse;
import shympyo.map.dto.NearbyMapResponse;
import shympyo.map.dto.PlaceDetailResponse;
import shympyo.map.repository.MapRepository;
import shympyo.rental.domain.Place;
import shympyo.rental.repository.PlaceRepository;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import static java.util.stream.Stream.concat;

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

    public List<NearbyMapResponse> findNearby(double lat, double lon, int radiusMeters, int limit, List<PlaceType> includeTypes){

        final int radius   = Math.max(1, Math.min(radiusMeters, 5_000)); // 1m ~ 5km
        final int maxLimit = Math.max(1, Math.min(limit, 200));

        BoundingBox box = calculateBoundingBox(lat, lon, radius);

        final List<PlaceType> types = (includeTypes == null || includeTypes.isEmpty())
                ? DEFAULT_TYPES
                : List.copyOf(includeTypes);

        boolean includeProvided = types.contains(PlaceType.USER_SHELTER);


        List<Map> maps = (includeTypes == null || includeTypes.isEmpty())
                ? mapRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon)
                : mapRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon, types);


        List<Place> places = includeProvided
                ? placeRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon)
                : List.of();


        record Cand(long id, double lat_, double lon_, PlaceType type, double dist) {}

        var mapCands = maps.stream()
                .map(m -> new Cand(
                        m.getId(),
                        m.getLatitude(),
                        m.getLongitude(),
                        m.getType(),
                        haversine(lat, lon, m.getLatitude(), m.getLongitude())
                ));

        var placeCands = places.stream()
                .map(p -> new Cand(
                        p.getId(),
                        p.getLatitude(),
                        p.getLongitude(),
                        PlaceType.USER_SHELTER,
                        haversine(lat, lon, p.getLatitude(), p.getLongitude())
                ));

        return concat(mapCands, placeCands)
                .filter(c -> c.dist <= radius)
                .sorted(Comparator.comparingDouble(c -> c.dist))
                .limit(maxLimit)
                .map(c -> new NearbyMapResponse(
                        c.id,
                        c.lat_,
                        c.lon_,
                        c.type
                ))
                .toList();
    }

    public List<NearbyListResponse> findNearbyList(double lat, double lon, int radiusMeters, int limit) {
        return findNearbyList(lat, lon, radiusMeters, limit, DEFAULT_TYPES);
    }


    public List<NearbyListResponse> findNearbyList(double lat, double lon, int radiusMeters, int limit, List<PlaceType> includeTypes) {

        final int radius   = Math.max(1, Math.min(radiusMeters, 5_000));
        final int maxLimit = Math.max(1, Math.min(limit, 200));

        BoundingBox box = calculateBoundingBox(lat, lon, radius);

        final List<PlaceType> types = (includeTypes == null || includeTypes.isEmpty())
                ? DEFAULT_TYPES
                : List.copyOf(includeTypes);

        final List<PlaceType> mapOnlyTypes = types.stream()
                .filter(t -> t != PlaceType.USER_SHELTER)
                .toList();

        final List<Map> maps = mapOnlyTypes.isEmpty()
                ? mapRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon)
                : mapRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon, mapOnlyTypes);

        final boolean includeProvided = types.contains(PlaceType.USER_SHELTER);

        final List<Place> places = includeProvided
                ? placeRepository.findInBoundingBox(box.minLat, box.maxLat, box.minLon, box.maxLon)
                : List.of();

        record Cand(
                long id,
                String name,
                String address,
                String content,
                PlaceType type,
                double dist
        ) {}

        var mapCands = maps.stream()
                .map(m -> new Cand(
                        m.getId(),
                        m.getName(),
                        m.getAddress(),
                        m.getContent(),
                        m.getType(),
                        haversine(lat, lon, m.getLatitude(), m.getLongitude())
                ));

        var placeCands = places.stream()
                .map(p -> new Cand(
                        p.getId(),
                        p.getName(),
                        p.getAddress(),
                        p.getContent(),
                        PlaceType.USER_SHELTER,
                        haversine(lat, lon, p.getLatitude(), p.getLongitude())
                ));

        return concat(mapCands, placeCands)
                .filter(c -> c.dist <= radius)
                .sorted(Comparator.comparingDouble(c -> c.dist))
                .limit(maxLimit)
                .map(c -> new NearbyListResponse(
                        c.id,
                        c.name,
                        c.address,
                        c.content,
                        c.type,
                        c.dist
                ))
                .toList();

    }

    public PlaceDetailResponse getMap(Long mapId){

        Map map = mapRepository.findById(mapId)
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

    public PlaceDetailResponse getPlace(Long placeId){

        Place place = placeRepository.findById(placeId)
                .orElseThrow(()-> new IllegalArgumentException("해당 장소가 없습니다."));

        return PlaceDetailResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .type(PlaceType.USER_SHELTER)
                .longitude(place.getLongitude())
                .address(place.getAddress())
                .latitude(place.getLatitude())
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

        final int radius = Math.max(1, Math.min(radiusMeters, 5_000));

        final double latDeg = radius / 111_320.0;
        double cosLat = Math.cos(Math.toRadians(lat));
        if (Math.abs(cosLat) < 1e-12) cosLat = 1e-12;
        final double lonDeg = radius / (111_320.0 * cosLat);

        return new BoundingBox(
                lat - latDeg, lat + latDeg,
                lon - lonDeg, lon + lonDeg
        );
    }
}