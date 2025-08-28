package map_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapDistanceResponse {
    private int mapId;
    private String name;
    private double distance;
}