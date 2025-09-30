package shympyo.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.map.domain.PlaceType;
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
            description = """
    위도(lat), 경도(lon) 좌표를 기준으로 반경 내 장소를 지도용으로 조회한다.  

    - `radius` : 검색 반경 (미터), 최소 1m ~ 최대 5km  
    - `limit` : 조회 최대 개수, 최대 200  
    - `types` : 조회할 장소 타입 필터 (예: CULTURE, SHELTER, USER_SHELTER 등).  
      - 여러 개 지정 가능 → `types=CULTURE&types=SHELTER`  
      - 쉼표 구분도 가능 → `types=CULTURE,SHELTER`  
      - 비워두면 모든 타입 조회
    """
    )
    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<NearbyMapResponse>>> nearby(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam double lat,

            @Parameter(description = "경도", example = "126.9780")
            @RequestParam double lon,

            @Parameter(description = "검색 반경 (m)", example = "200")
            @RequestParam(defaultValue = "100") int radius,

            @Parameter(description = "조회 최대 개수", example = "50")
            @RequestParam(defaultValue = "100") int limit,

            @Parameter(
                    description = "조회할 타입 목록. 예: types=CULTURE,SHELTER or types=CULTURE&types=SHELTER",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = shympyo.map.domain.PlaceType.class)
                    )
            )
            @RequestParam(name = "types", required = false) List<PlaceType> types
    ) {
        return ResponseUtil.success(mapService.findNearby(lat, lon, radius, limit, types));
    }



    @Operation(
            summary = "주변 장소 목록 조회",
            description = """
    위도(lat), 경도(lon) 좌표를 기준으로 반경 내 장소를 리스트 형태로 조회한다.  

    - `radius` : 검색 반경 (미터), 최소 1m ~ 최대 5km  
    - `limit` : 조회 최대 개수, 최대 200  
    - `types` : 조회할 장소 타입 필터 (예: CULTURE, SHELTER, USER_SHELTER 등).  
      - 여러 개 지정 가능 → `types=CULTURE&types=SHELTER`  
      - 쉼표 구분도 가능 → `types=CULTURE,SHELTER`  
      - 비워두면 모든 타입 조회
    """
    )
    @GetMapping("/nearby-list")
    public ResponseEntity<CommonResponse<List<NearbyListResponse>>> nearbyList(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam double lat,

            @Parameter(description = "경도", example = "126.9780")
            @RequestParam double lon,

            @Parameter(description = "검색 반경 (m)", example = "200")
            @RequestParam(defaultValue = "100") int radius,

            @Parameter(description = "조회 최대 개수", example = "50")
            @RequestParam(defaultValue = "50") int limit,

            @Parameter(
                    description = "조회할 타입 목록. 여러 개 가능 (예: CULTURE,SHELTER)",
                    array = @ArraySchema(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = shympyo.map.domain.PlaceType.class)
                    )
            )
            @RequestParam(name = "types", required = false) List<PlaceType> types
    ) {
        return ResponseUtil.success(mapService.findNearbyList(lat, lon, radius, limit, types));
    }


    @Operation(
            summary = "공공 쉼터 상세 조회",
            description = "공공 데이터(Map)에 등록된 쉼터의 상세 정보를 조회한다."
    )
    @GetMapping("/public/{id}")
    public ResponseEntity<CommonResponse<PlaceDetailResponse>> getMap(
            @Parameter(description = "공공 쉼터 ID", example = "123")
            @PathVariable Long id
    ) {
        return ResponseUtil.success(mapService.getMap(id));
    }

    @Operation(
            summary = "사용자 제공 쉼터 상세 조회",
            description = "민간 데이터(Place)에 등록된 쉼터의 상세 정보를 조회한다."
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<CommonResponse<PlaceDetailResponse>> getPlace(
            @PathVariable Long id
    ) {
        return ResponseUtil.success(mapService.getPlace(id));
    }
}
