package shympyo.map.dto;

import lombok.Builder;
import lombok.Getter;
import shympyo.map.domain.PlaceType;

@Getter
@Builder
public class PlaceDetailResponse {

    private Long id;

    private String name;

    private String address;

    private String content;

    private double latitude;

    private double longitude;

    private PlaceType type;


}
