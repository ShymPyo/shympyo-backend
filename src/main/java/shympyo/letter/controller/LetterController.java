package shympyo.letter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.letter.dto.CountLetterResponse;
import shympyo.letter.dto.LetterResponse;
import shympyo.letter.dto.SendLetterRequest;
import shympyo.letter.dto.SendLetterResponse;
import shympyo.letter.service.LetterService;

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
    @GetMapping("/received")
    public ResponseEntity<CommonResponse<List<LetterResponse>>> getReceivedLetters(
            @AuthenticationPrincipal CustomUserDetails owner) {
        return ResponseUtil.success(letterService.getReceivedLetters(owner.getId()));
    }

    @Operation(
            summary = "편지 보내기",
            description = "휴식을 한 장소로 편지를 전송한다."
    )
    @PostMapping("/send")
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
    public ResponseEntity<CommonResponse<Void>> markRead(
            @AuthenticationPrincipal CustomUserDetails owner,
            @Parameter(description = "편지 ID", example = "101") @PathVariable Long letterId) {

        letterService.readLetter(owner.getId(), letterId);
        return ResponseUtil.success("읽음 처리되었습니다.", null);
    }

    @Operation(
            summary = "받은 편지 개수 조회",
            description = "대여 장소를 제공한 제공자가 받은 편지의 개수를 조회한다."
    )
    @GetMapping("/count")
    public ResponseEntity<CommonResponse<CountLetterResponse>> count(
            @AuthenticationPrincipal CustomUserDetails owner) {

        CountLetterResponse response = letterService.countLetter(owner.getId());
        return ResponseUtil.success("받은 편지 개수 조회에 성공했습니다", response);
    }
}
