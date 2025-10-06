package shympyo.report.dto;


import lombok.*;
import shympyo.report.domain.SanctionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderBlockUserResponse {

    private Long sanctionId;
    private Long userId;
    private String nickname;
    private LocalDateTime startAt;
    private SanctionStatus status;

}
