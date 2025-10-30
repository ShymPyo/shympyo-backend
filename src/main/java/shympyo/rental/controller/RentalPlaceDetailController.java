package shympyo.rental.controller;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import shympyo.rental.dto.external.RentalPlaceDetailDto;
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
}
