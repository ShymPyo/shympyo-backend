package account_service.rental.controller;

import account_service.auth.user.CustomUserDetails;
import account_service.global.response.ResponseUtil;
import account_service.rental.dto.EnterRequest;
import account_service.rental.dto.ExitResponse;
import account_service.rental.dto.RentalResponse;
import account_service.rental.service.RentalService;
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

public class RentalController {

    private final RentalService rentalService;


    @PostMapping("/enter")
    public ResponseEntity<?> enter(@RequestBody EnterRequest request,
                                   @AuthenticationPrincipal CustomUserDetails user){

        RentalResponse response = rentalService.startRental(user.getId(), request.getPlaceCode());

        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/exit")
    public ResponseEntity<?> exit(@AuthenticationPrincipal CustomUserDetails user){

        ExitResponse rental = rentalService.endRental(user.getId());

        return ResponseEntity.ok(ResponseUtil.success(rental));
    }
}
