package shympyo.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shympyo.map.domain.PlaceType;

@Getter
@AllArgsConstructor
public class NearbyListResponse {

    private Long id;

    private String name;

    private String address;

    private String content;

    private PlaceType type;

    private double distanceM;
}
