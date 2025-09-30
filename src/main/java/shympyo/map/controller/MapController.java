package shympyo.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.map.dto.NearbyListResponse;
import shympyo.map.dto.NearbyMapResponse;
import shympyo.map.dto.PlaceDetailResponse;
import shympyo.map.service.MapService;

import java.util.List;

@Tag(name = "Map", description = "지도/위치 기반 API")
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @Operation(summary = "주변 장소 지도 조회")
    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<NearbyMapResponse>>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "100") int radius,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return ResponseUtil.success(mapService.findNearby(lat, lon, radius, limit));
    }

    @Operation(summary = "주변 장소 목록 조회")
    @GetMapping("/nearby-list")
    public ResponseEntity<CommonResponse<List<NearbyListResponse>>> nearbyList(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "100") int radius,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseUtil.success(mapService.findNearbyList(lat, lon, radius, limit));
    }

    @Operation(summary = "공공 쉼터 상세 조회")
    @GetMapping("/public/{id}")
    public ResponseEntity<CommonResponse<PlaceDetailResponse>> getMap(
            @PathVariable Long id
    ) {
        return ResponseUtil.success(mapService.getMap(id));
    }

    @Operation(summary = "사용자 제공 쉼터 상세 조회")
    @GetMapping("/user/{id}")
    public ResponseEntity<CommonResponse<PlaceDetailResponse>> getPlace(
            @PathVariable Long id
    ) {
        return ResponseUtil.success(mapService.getPlace(id));
    }
}
