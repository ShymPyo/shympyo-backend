package shympyo.letter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.CursorPageResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.letter.dto.*;
import shympyo.letter.service.LetterService;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Letter", description = "편지(쪽지) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/letters")
public class LetterController {

    private final LetterService letterService;

    @Operation(
            summary = "받은 편지함 조회",
            description = "장소를 제공한 제공자가 가게로 받은 편지 목록을 조회한다."
    )
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<CursorPageResponse<LetterHistoryResponse>>> getReceivedLetters(
            @AuthenticationPrincipal CustomUserDetails owner,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size)
    {

        return ResponseUtil.success("받은 편지 목록", letterService.getReceivedLetters(owner.getId(), cursorCreatedAt, cursorId, size));
    }

    @GetMapping("/{letterId}")
    public ResponseEntity<CommonResponse<LetterDetailResponse>> getDetailReceivedLetters(
            @AuthenticationPrincipal CustomUserDetails owner,
            @PathVariable Long letterId
    ) {
        return ResponseUtil.success(letterService.getReceivedLetterDetail(owner.getId(),letterId));
    }

    @Operation(
            summary = "받은 편지 개수 조회",
            description = "대여 장소를 제공한 제공자가 받은 편지의 개수를 조회한다."
    )
    @GetMapping("/count")
    public ResponseEntity<CommonResponse<LetterCountResponse>> count(
            @AuthenticationPrincipal CustomUserDetails owner) {

        LetterCountResponse response = letterService.countLetter(owner.getId());
        return ResponseUtil.success("받은 편지 개수 조회에 성공했습니다", response);
    }


    @Operation(
            summary = "편지 보내기",
            description = "휴식을 한 장소로 편지를 전송한다."
    )
    @PostMapping
    public ResponseEntity<CommonResponse<SendLetterResponse>> send(
            @AuthenticationPrincipal CustomUserDetails writer,
            @Valid @RequestBody SendLetterRequest request) {

        SendLetterResponse response = letterService.send(writer.getId(), request);
        return ResponseUtil.success("성공적으로 편지를 보냈습니다.", response);
    }

    @Operation(
            summary = "편지 읽음 처리",
            description = "특정 편지를 읽음 상태로 변경한다."
    )
    @PostMapping("/{letterId}/read")
    public ResponseEntity<CommonResponse<Void>> read(
            @AuthenticationPrincipal CustomUserDetails owner,
            @Parameter(description = "편지 ID", example = "101") @PathVariable Long letterId) {

        letterService.readLetter(owner.getId(), letterId);
        return ResponseUtil.success("읽음 처리되었습니다.", null);
    }

}
