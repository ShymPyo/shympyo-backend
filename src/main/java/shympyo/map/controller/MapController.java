package shympyo.map.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.map.dto.NearbyMapDto;
import shympyo.map.service.MapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<NearbyMapDto>>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "100") int radius, // meter
            @RequestParam(defaultValue = "100") int limit
    ) {
        return ResponseUtil.success(mapService.findNearby(lat, lon, radius, limit));
    }
}