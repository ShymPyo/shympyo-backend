package shympyo.report.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shympyo.report.domain.PlaceType;
import shympyo.report.domain.SanctionScope;
import shympyo.report.domain.SanctionType;
import shympyo.report.repository.SanctionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SanctionQueryService {

    private final SanctionRepository sanctionRepository;

    public boolean isCategoryBlocked(Long userId, PlaceType placeType, LocalDateTime at) {
        return sanctionRepository.existsActiveCategoryBlock(
                userId,
                SanctionType.BLOCK_CONTENT,
                SanctionScope.PLACE_CATEGORY,
                placeType,
                at
        );
    }

    public boolean isPlaceBlocked(Long userId, Long placeId, LocalDateTime at) {
        return sanctionRepository.existsActivePlaceBlock(
                userId,
                SanctionType.BLOCK_CONTENT,
                SanctionScope.PLACE_ONLY,
                placeId,
                at
        );
    }
}
