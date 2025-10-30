package shympyo.map.port.report.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shympyo.map.domain.PlaceType;
import shympyo.map.port.report.SanctionAccess;
import shympyo.report.domain.SanctionScope;
import shympyo.report.domain.SanctionType;
import shympyo.report.repository.SanctionRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SanctionAccessImpl implements SanctionAccess {

    private final SanctionRepository sanctionRepository;

    @Override
    public boolean isCategoryBlocked(Long userId, PlaceType placeType, LocalDateTime now) {
        return sanctionRepository.existsActiveCategoryBlock(
                userId,
                SanctionType.BLOCK_CONTENT,
                SanctionScope.PLACE_CATEGORY,
                placeType,
                now
        );
    }

    @Override
    public boolean isPlaceBlocked(Long userId, Long placeId, LocalDateTime now) {
        return sanctionRepository.existsActivePlaceBlock(
                userId,
                SanctionType.BLOCK_CONTENT,
                SanctionScope.PLACE_ONLY,
                placeId,
                now
        );
    }
}
