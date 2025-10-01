package shympyo.rental.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessHoursResponse {

    private Long placeId;

    private List<BusinessHourItemResponse> items;

}
