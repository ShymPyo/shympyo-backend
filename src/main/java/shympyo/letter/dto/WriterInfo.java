package shympyo.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WriterInfo {

    private Long id;
    private String name;
    private String email;
    private String phone;

}
