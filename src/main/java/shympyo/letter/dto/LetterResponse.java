package shympyo.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class LetterResponse {

    private Long id;
    private Long placeId;
    String placeName;
    WriterInfo writerInfo;
    String content;
    boolean isRead;
    LocalDateTime readAt;
    LocalDateTime createdAt;
}
