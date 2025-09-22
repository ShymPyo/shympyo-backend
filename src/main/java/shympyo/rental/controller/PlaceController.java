package shympyo.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.rental.dto.*;
import shympyo.rental.service.PlaceService;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Tag(name = "Place", description = "장소 관리 API")
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    @Operation(summary = "쉼터 생성", description = "장소 제공자가 새로운 장소를 등록한다.")
    public ResponseEntity<CommonResponse<PlaceResponse>> createPlace(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid PlaceCreateRequest request
    ) {
        Long userId = user.getId();
        PlaceResponse place = placeService.createPlace(userId, request);

        return ResponseUtil.success("성공적으로 쉼터를 생성했습니다.", place);
    }

    @PatchMapping
    @Operation(summary = "쉼터 수정", description = "쉼터 제공자 쉼터 정보를 수정한다.")
    public ResponseEntity<CommonResponse<PlaceResponse>> updatePlace(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid PlaceUpdateRequest request
    ){
      placeService.updatePlace(user.getId(), request);

      return ResponseUtil.success("성공적으로 쉼터 수정했습니다.");
    }

    @GetMapping
    @Operation(summary = "쉼터 조회", description = "장소 제공자가 자신의 쉼터 정보를 조회할 수 있다.")
    public ResponseEntity<CommonResponse<PlaceResponse>> getPlace(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId();
        PlaceResponse place = placeService.getPlace(userId);

        return ResponseUtil.success("성공적으로 쉼터를 조회했습니다.", place);
    }


}
