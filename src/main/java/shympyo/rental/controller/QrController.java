package shympyo.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import shympyo.rental.domain.Place;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.service.QrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "QR", description = "QR 이미지 및 코드 확인 API")
public class QrController {

    private final PlaceRepository placeRepository;

    private static final String PUBLIC_BASE_URL = "https://app.example.com"; // 실제 도메인으로 변경

    @Operation(
            summary = "장소 고정 QR 이미지(PNG)",
            description = "장소 코드가 인코딩된 QR 이미지를 PNG로 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "QR PNG 반환",
                            content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE,
                                    schema = @Schema(type = "string", format = "binary"))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 장소")
            },
            security = {}
    )
    @GetMapping(value = "/places/{placeId}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] qr(@PathVariable Long placeId) {
        Place p = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소"));
        String url = QrGenerator.enterUrl(PUBLIC_BASE_URL, p.getCode());
        return QrGenerator.toPng(url, 512);
    }


    @Operation(
            summary = "QR 코드 확인",
            description = "사용자가 QR를 찍으면 장소 인증 코드가 나오고, 그 코드를 포함하여 보내면 유효한지 확인하고 그대로 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효한 코드",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EnterCodeResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 장소 코드")
            }
    )
    @GetMapping(value = "/enter-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnterCodeResponse> enterCode(@RequestParam("c") String code) {
        placeRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 장소 코드"));
        return ResponseEntity.ok(new EnterCodeResponse(code));
    }

    @Schema(description = "입장용 장소 코드 응답")
    public record EnterCodeResponse(String placeCode) {}
}
