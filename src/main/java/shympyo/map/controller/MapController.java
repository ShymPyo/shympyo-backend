package shympyo.map.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.map.dto.NearbyListResponse;
import shympyo.map.dto.NearbyMapResponse;
import shympyo.map.dto.PlaceDetailResponse;
import shympyo.map.service.MapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<NearbyMapResponse>>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "100") int radius, // meter
            @RequestParam(defaultValue = "100") int limit
    ) {
        return ResponseUtil.success(mapService.findNearby(lat, lon, radius, limit));
    }

    @GetMapping("/nearby-list")
    public  ResponseEntity<CommonResponse<List<NearbyListResponse>>> nearbyList(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "100") int radius,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseUtil.success(mapService.findNearbyList(lat, lon, radius, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PlaceDetailResponse>> place(@PathVariable Long id){

        return ResponseUtil.success(mapService.findPlace(id));
    }
}