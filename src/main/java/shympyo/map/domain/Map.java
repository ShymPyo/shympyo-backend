package shympyo.map.domain;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100)
    private String content;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    @Column(length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaceType type;

}