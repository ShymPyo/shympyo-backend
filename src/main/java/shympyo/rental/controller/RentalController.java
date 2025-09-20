package shympyo.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.ResponseUtil;
import shympyo.rental.dto.EnterRequest;
import shympyo.rental.dto.ExitResponse;
import shympyo.rental.dto.RentalResponse;
import shympyo.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
@Tag(name = "Rental", description = "QR 기반 대여(입장/퇴장) API")

public class RentalController {

    private final RentalService rentalService;

    @Operation(
            summary = "입장 처리",
            description = "QR 코드(placeCode)를 이용해 장소에 입장한다."
    )
    @PostMapping("/enter")
    public ResponseEntity<?> enter(@RequestBody EnterRequest request,
                                   @AuthenticationPrincipal CustomUserDetails user){

        RentalResponse response = rentalService.startRental(user.getId(), request.getPlaceCode());

        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @Operation(
            summary = "퇴장 처리",
            description = "현재 대여 중인 장소에서 퇴장한다."
    )
    @PostMapping("/exit")
    public ResponseEntity<?> exit(@AuthenticationPrincipal CustomUserDetails user){

        ExitResponse rental = rentalService.endRental(user.getId());

        return ResponseEntity.ok(ResponseUtil.success(rental));
    }
}
