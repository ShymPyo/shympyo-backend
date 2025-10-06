package shympyo.global.sse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shympyo.auth.user.CustomUserDetails;
import shympyo.rental.repository.PlaceRepository;

@RestController
@RequiredArgsConstructor
public class RentalSseController {

    private final SseEmitterHub hub;
    private final PlaceRepository placeRepository;

    @Operation(
            summary = "장소 SSE 구독",
            description = """
            해당 placeId 채널을 SSE로 구독한다.
            응답은 'text/event-stream' 이며 연결을 유지한다.
            성공 시 최초로 'event: hello\\ndata: connected' 이벤트가 전송된다.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 스트림 시작",
                    content = @Content(
                            mediaType = "text/event-stream",
                            examples = @ExampleObject(
                                    name = "예시 스트림",
                                    value = "event: hello\ndata: connected\n\nevent: rental-started\ndata: {\"rentalId\":5,\"placeId\":3}\n\n"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "장소 없음")
    })
    @GetMapping(value = "/sse/places/{placeId}", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails provider,
                                @PathVariable Long placeId) {

        if(!placeRepository.existsByIdAndOwnerId(placeId, provider.getId())){
            throw new IllegalArgumentException("해당 장소가 존재하지 않습니다.");
        };

        SseEmitter emitter = new SseEmitter(0L);
        return hub.add(placeId, emitter);
    }
}
