package shympyo.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shympyo.rental.domain.Place;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class PlaceResponse {

    private Long id;

    private String name;

    private String content;

    private String imageUrl;

    private Integer maxCapacity;

    private Double latitude;

    private Double longitude;

    private String address;

    private LocalTime openTime;

    private LocalTime closeTime;

    private DayOfWeek weeklyHoliday;


    public static PlaceResponse from(Place place) {
        return PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .content(place.getContent())
                .imageUrl(place.getImageUrl())
                .maxCapacity(place.getMaxCapacity())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .address(place.getAddress())
                .openTime(place.getOpenTime())
                .closeTime(place.getCloseTime())
                .weeklyHoliday(place.getWeeklyHoliday())
                .build();
    }
}
