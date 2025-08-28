package map_service.controller;

import map_service.dto.MapResponse;
import map_service.dto.MapDistanceResponse;
import map_service.service.MapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/all")
    public List<MapResponse> getAllMaps() {
        return mapService.getAllMaps();
    }

    @GetMapping("/distances")
    public List<MapDistanceResponse> getDistances(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        return mapService.getDistances(latitude, longitude);
    }


}