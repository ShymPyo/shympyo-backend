package shympyo.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shympyo.map.domain.PlaceType;

@Getter
@AllArgsConstructor
public class NearbyMapResponse {

    private Long id;

    private double latitude;

    private double longitude;

    private PlaceType type;

}
