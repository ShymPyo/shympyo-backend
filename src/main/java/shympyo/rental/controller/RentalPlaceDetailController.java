package shympyo.rental.controller;

import java.time.LocalDateTime;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import shympyo.rental.dto.external.RentalPlaceDetailDto;
import shympyo.rental.dto.external.RentalPlaceDto;
import shympyo.rental.service.RentalMapQueryService;

@RestController
@RequestMapping("/api/rental/places")
@RequiredArgsConstructor
public class RentalPlaceDetailController {

    private final RentalMapQueryService rentalMapQueryService;

    @GetMapping("/{placeId}")
    public RentalPlaceDetailDto getPlaceDetail(
            @PathVariable Long placeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime at
    ) {
        return rentalMapQueryService.getPlaceDetail(placeId, at);
    }

    @GetMapping("/box")
    public List<RentalPlaceDto> getPlacesInBox(
            @RequestParam double minLat,
            @RequestParam double maxLat,
            @RequestParam double minLon,
            @RequestParam double maxLon,
            @RequestParam Long userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime at
    ) {
        return rentalMapQueryService.findUserSheltersInBox(
                minLat, maxLat,
                minLon, maxLon,
                userId,
                at
        );
    }
}
