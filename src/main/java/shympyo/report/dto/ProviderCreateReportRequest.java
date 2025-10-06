package shympyo.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import shympyo.report.domain.ReportReason;

@Getter
public class ProviderCreateReportRequest {

    @NotNull
    private Long reportedUserId;

    private Long rentalId;

    @NotNull
    private ReportReason reason;

    private String content;
}
