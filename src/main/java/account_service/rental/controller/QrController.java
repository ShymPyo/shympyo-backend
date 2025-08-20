package account_service.rental.controller;

import account_service.rental.domain.Place;
import account_service.rental.repository.PlaceRepository;
import account_service.qr.QrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QrController {

    private final PlaceRepository placeRepository;

    private static final String PUBLIC_BASE_URL = "https://app.example.com"; // 실제 도메인으로 변경

    /** 장소 코드가 들어간 고정 QR 이미지(PNG) */
    @GetMapping(value = "/places/{placeId}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] qr(@PathVariable Long placeId) {
        Place p = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소"));
        String url = QrGenerator.enterUrl(PUBLIC_BASE_URL, p.getCode());
        return QrGenerator.toPng(url, 512);
    }


    /** QR이 가리킬 엔드포인트: /api/enter-code?c=PLACE_CODE
     *  반환값: {"placeCode":"PLACE-A-001"} */
    @GetMapping(value = "/enter-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnterCodeResponse> enterCode(@RequestParam("c") String code) {
        // 유효한 코드인지(=존재하는 place인지) 확인만 하고 코드 그대로 돌려줌
        placeRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 장소 코드"));
        return ResponseEntity.ok(new EnterCodeResponse(code));
    }

    public record EnterCodeResponse(String placeCode) {}
}
