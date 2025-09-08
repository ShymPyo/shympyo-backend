package shympyo.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shympyo.map.domain.PlaceType;

@Getter
@AllArgsConstructor
public class NearbyMapDto {

    private Long id;

    private String name;

    private String address;

    private PlaceType type;

    private double latitude;

    private double longitude;

    private double distanceM;
}
