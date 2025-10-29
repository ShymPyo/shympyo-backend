package shympyo.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import shympyo.user.dto.PresignRequest;
import shympyo.user.dto.PresignResponse;
import shympyo.user.service.ProfileImageService;

@RestController
@RequestMapping("/api/profile-image")
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @Operation(
            summary = "프로필 이미지 업로드용 presigned URL 발급",
            description = """
                    네이버 클라우드 Object Storage(NCP)에 프로필 이미지를 직접 업로드하기 위한
                    presigned PUT URL을 발급합니다.
                    
                    ### 업로드 전체 흐름
                    1. 이 API를 호출해서 uploadUrl / publicUrl / objectKey 를 받는다.
                    2. uploadUrl 로 이미지를 PUT 업로드한다 (백엔드로 파일을 안 보내도 됨).
                    3. 업로드가 끝나면 받은 publicUrl을 /api/users/me 같은 프로필 갱신 API에 넘겨서
                       최종 프로필 이미지 URL로 저장한다.
                    
                    요청 바디의 fileExtension, contentType 은 업로드할 이미지 파일 정보입니다.
                    userId 는 JWT에서 추출하므로 요청 바디로 줄 필요가 없습니다.
                    
                    ### PUT 업로드 시 주의사항 (매우 중요)
                    presigned URL은 특정 헤더까지 포함해서 서명되어 있습니다.
                    따라서 PUT 요청을 보낼 때 아래 헤더들을 반드시 포함해야 합니다.
                    
                    - `Content-Type`: presign 요청 시 넘겼던 contentType 값과 동일해야 합니다.
                       예) "image/jpeg"
                    - `x-amz-acl`: "public-read"
                    """,
            security = {
                    @SecurityRequirement(name = "bearerAuth")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "presigned URL 발급 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresignResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (contentType 등 유효하지 않음)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (JWT 누락 또는 만료)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류 혹은 presigned 생성 실패",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/presign")
    public ResponseEntity<CommonResponse<PresignResponse>> createPresignedUrl(
            @RequestBody @Valid PresignRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        PresignResponse response = profileImageService.createPresignedUpload(
                user.getId(),
                request.fileExtension(),
                request.contentType()
        );

        return ResponseUtil.success(response);
    }
}
