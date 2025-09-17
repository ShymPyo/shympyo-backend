package shympyo.letter.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/letters")
public class LetterController {

    private final LetterService letterService;


    @GetMapping("/received")
    public ResponseEntity<CommonResponse<List<LetterResponse>>> getReceivedLetters(
            @AuthenticationPrincipal CustomUserDetails owner){

        return ResponseUtil.success(letterService.getReceivedLetters(owner.getId()));
    }

    @PostMapping("/send")
    public ResponseEntity<CommonResponse<SendLetterResponse>> send(
            @AuthenticationPrincipal CustomUserDetails writer,
            @Valid @RequestBody SendLetterRequest request){

        SendLetterResponse response = letterService.send(writer.getId(), request);

        return ResponseUtil.success("성공적으로 편지를 보냈습니다.", response);
    }

    @PostMapping("/{letterId}/read")
    public ResponseEntity<CommonResponse<Void>> markRead(
            @AuthenticationPrincipal CustomUserDetails owner,
            @PathVariable Long letterId) {

        letterService.readLetter(owner.getId(), letterId);

        return ResponseUtil.success("읽음 처리되었습니다.", null);
    }

    @GetMapping("/count")
    public ResponseEntity<CommonResponse<CountLetterResponse>> count(
            @AuthenticationPrincipal CustomUserDetails onwer){

        CountLetterResponse response = letterService.countLetter(onwer.getId());

        return ResponseUtil.success("받은 편지 개수 조회에 성공했습니다", response);
    }

}
