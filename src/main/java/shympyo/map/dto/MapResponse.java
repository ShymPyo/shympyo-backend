package shympyo.map.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapResponse {
    private int mapId;
    private String name;
    private String description;
    private float longitude;
    private float latitude;
    private String address;
    private String type;
}