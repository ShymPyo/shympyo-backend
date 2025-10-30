package shympyo.map.port.report;

import java.time.LocalDateTime;
import shympyo.map.domain.PlaceType;

public interface SanctionAccess {

    boolean isCategoryBlocked(Long userId, PlaceType placeType, LocalDateTime now);

    boolean isPlaceBlocked(Long userId, Long placeId, LocalDateTime now);

}
