package shympyo.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.rental.dto.PlaceImagePresignRequest;
import shympyo.storage.dto.PresignResponse;
import shympyo.storage.service.ImagePresignService;

@RestController
@RequestMapping("/api/place-image")
@RequiredArgsConstructor
public class PlaceImageController {

    private final ImagePresignService profileImageService;

    @Operation(
            summary = "장소 이미지 업로드용 presigned URL 발급",
            description = """
                    Naver Cloud Object Storage에 장소 이미지를 직접 업로드할 수 있는 presigned PUT URL을 발급합니다.
                    
                    **요청 흐름**
                    1. 이 API 호출 → uploadUrl, publicUrl, objectKey 반환
                    2. 클라이언트가 uploadUrl로 PUT 업로드
                    3. 업로드 완료 후 publicUrl을 place 수정 API로 전달하여 저장
                    4. places의 쉼터 수정 이용해서 imageUrl 수정 필요
                    
                    **주의사항**
                    - 업로드 시 헤더:
                      - Content-Type: 요청 시 전달한 값과 동일 (예: image/jpeg)
                      - x-amz-acl: public-read
                    - 헤더 불일치 시 403(SignatureDoesNotMatch) 발생
                    - 로그인한 사용자와 place의 owner가 일치해야 함
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "presigned URL 발급 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PresignResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서명 생성 실패")
            }
    )
    @PostMapping("/presign")
    public ResponseEntity<CommonResponse<PresignResponse>> createPlaceImagePresign(
            @RequestBody @Valid PlaceImagePresignRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        PresignResponse response = profileImageService.createPlaceImagePresign(
                request.placeId(),
                user.getId(),
                request.fileExtension(),
                request.contentType()
        );

        return ResponseUtil.success(response);
    }
}
