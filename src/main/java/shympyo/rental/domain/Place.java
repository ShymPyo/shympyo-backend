package shympyo.rental.domain;

import jakarta.persistence.*;
import lombok.*;
import shympyo.rental.dto.PlaceUpdateRequest;
import shympyo.user.domain.User;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String content;

    @Column(length = 500)
    private String imageUrl;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 200)
    private String address;

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_place_owner"))
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaceStatus status = PlaceStatus.INACTIVE;

    public void updatePatch(PlaceUpdateRequest request) {
        if (request.getName() != null) this.name = request.getName();
        if (request.getContent() != null) this.content = request.getContent();
        if (request.getImageUrl() != null) this.imageUrl = request.getImageUrl();
        if (request.getMaxCapacity() != null) this.maxCapacity = request.getMaxCapacity();
        if (request.getAddress() != null) this.address = request.getAddress();
    }

    public void activate()   { this.status = PlaceStatus.ACTIVE; }
    public void deactivate() { this.status = PlaceStatus.INACTIVE; }
    public void maintenance() { this.status = PlaceStatus.MAINTENANCE; }
    public void delete() { this.status = PlaceStatus.DELETED; }
}
