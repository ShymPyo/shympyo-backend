package shympyo.global.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shympyo.auth.user.CustomUserDetails;
import shympyo.rental.repository.PlaceRepository;
import shympyo.user.repository.UserRepository;

@RestController
@RequiredArgsConstructor
public class RentalSseController {

    private final SseEmitterHub hub;
    private final PlaceRepository placeRepository;

    @GetMapping(value = "/sse/places/{placeId}", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails provider,
                                @PathVariable Long placeId) {

        if(!placeRepository.existsByIdAndOwnerId(provider.getId(), placeId)){
            throw new IllegalArgumentException("해당 장소가 존재하지 않습니다.");
        };

        SseEmitter emitter = new SseEmitter(0L);
        return hub.add(placeId, emitter);
    }
}
