package shympyo.rental.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessHoursRequest {

    @NotEmpty(message = "요일별 항목은 최소 1개 이상이어야 합니다.")
    @Valid
    private List<BusinessHourUpsertRequest> items;
}