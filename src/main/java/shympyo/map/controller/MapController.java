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

    @Operation(
            summary = "주변 장소 지도 조회",
            description = "위도(lat), 경도(lon) 좌표를 기준으로 반경 내 장소를 지도 형태로 조회한다."
    )
    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<NearbyMapResponse>>> nearby(
            @Parameter(description = "위도", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도", example = "126.9780") @RequestParam double lon,
            @Parameter(description = "검색 반경 (미터)", example = "200") @RequestParam(defaultValue = "100") int radius,
            @Parameter(description = "조회 최대 개수", example = "50") @RequestParam(defaultValue = "100") int limit
    ) {
        return ResponseUtil.success(mapService.findNearby(lat, lon, radius, limit));
    }

    @Operation(
            summary = "주변 장소 목록 조회",
            description = "위도(lat), 경도(lon) 좌표를 기준으로 반경 내 장소를 리스트 형태로 조회한다."
    )
    @GetMapping("/nearby-list")
    public ResponseEntity<CommonResponse<List<NearbyListResponse>>> nearbyList(
            @Parameter(description = "위도", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도", example = "126.9780") @RequestParam double lon,
            @Parameter(description = "검색 반경 (미터)", example = "200") @RequestParam(defaultValue = "100") int radius,
            @Parameter(description = "조회 최대 개수", example = "20") @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseUtil.success(mapService.findNearbyList(lat, lon, radius, limit));
    }

    @Operation(
            summary = "장소 상세 조회",
            description = "장소 ID를 사용해 상세 정보를 조회한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PlaceDetailResponse>> place(
            @Parameter(description = "장소 ID", example = "123") @PathVariable Long id
    ) {
        return ResponseUtil.success(mapService.findPlace(id));
    }
}
