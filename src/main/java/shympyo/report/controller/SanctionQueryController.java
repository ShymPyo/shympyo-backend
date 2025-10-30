package shympyo.report.controller;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import shympyo.report.domain.PlaceType;
import shympyo.report.service.SanctionQueryService;

@RestController
@RequestMapping("/api/report/sanctions")
@RequiredArgsConstructor
public class SanctionQueryController {

    private final SanctionQueryService sanctionQueryService;

    @GetMapping("/category-block")
    public boolean isCategoryBlocked(
            @RequestParam Long userId,
            @RequestParam PlaceType placeType,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime at
    ) {
        return sanctionQueryService.isCategoryBlocked(userId, placeType, at);
    }

    @GetMapping("/place-block")
    public boolean isPlaceBlocked(
            @RequestParam Long userId,
            @RequestParam Long placeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime at
    ) {
        return sanctionQueryService.isPlaceBlocked(userId, placeId, at);
    }
}
