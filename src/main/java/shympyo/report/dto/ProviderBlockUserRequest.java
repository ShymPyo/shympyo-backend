package shympyo.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shympyo.report.domain.SanctionReason;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderBlockUserRequest {


    @NotNull
    private Long placeId;

    private SanctionReason reason = SanctionReason.OTHER;

    private String detail;

    private Integer durationDays;
}
