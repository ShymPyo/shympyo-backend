package shympyo.report.dto;

import lombok.*;
import shympyo.report.domain.SanctionReason;
import shympyo.report.domain.SanctionStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderBlockUserDetailResponse {

    private Long sanctionId;
    private Long userId;
    private String nickname;
    private SanctionReason reason;
    private String detail;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private SanctionStatus status;

}
