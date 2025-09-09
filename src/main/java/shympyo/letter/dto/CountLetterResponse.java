package shympyo.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CountLetterResponse {

    private Long total;
    private Long unRead;
    private Long read;

}
