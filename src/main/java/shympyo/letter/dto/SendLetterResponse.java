package shympyo.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class SendLetterResponse {

    private Long id;
    private Long placeId;
    String placeName;
    Long writerId;
    String writeName;
    String content;
    boolean isRead;
    LocalDateTime readAt;
    LocalDateTime createdAt;
}
