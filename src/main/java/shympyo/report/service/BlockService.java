package shympyo.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.rental.domain.Place;
import shympyo.rental.repository.PlaceRepository;
import shympyo.report.domain.SanctionSource;
import shympyo.report.dto.ProviderBlockUserDetailResponse;
import shympyo.report.dto.ProviderBlockUserRequest;
import shympyo.report.dto.ProviderBlockUserResponse;
import shympyo.report.repository.SanctionRepository;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final SanctionRepository sanctionRepository;

    private final SanctionService sanctionService;

    public List<ProviderBlockUserResponse> getBlock(Long providerId) {

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Place place = placeRepository.findByOwnerId(provider.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 장소를 찾을 수 없습니다.") );

        return sanctionRepository.findActiveBlockedUsers(
                place.getId(),
                LocalDateTime.now()
        );
    }

    public ProviderBlockUserDetailResponse getBlockDetail(Long providerId, Long userId) {

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Place place = placeRepository.findByOwnerId(provider.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 장소를 찾을 수 없습니다.") );

        return sanctionRepository.findActiveBlockDetail(
                        place.getId(),
                        userId,
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new IllegalArgumentException("차단 상세 내역이 없습니다."));
    }

    @Transactional
    public Long blockUser(Long userId,Long targetUserId, ProviderBlockUserRequest req) {

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("장소 제공자만 차단할 수 있습니다.");
        }
        if (provider.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신은 차단할 수 없습니다.");
        }

        userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 대상 사용자를 찾을 수 없습니다."));
        if (!placeRepository.existsByIdAndOwnerId(req.getPlaceId(), provider.getId())) {
            throw new IllegalArgumentException("해당 장소는 이 제공자의 소유가 아닙니다.");
        }

        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = (req.getDurationDays() == null || req.getDurationDays() <= 0)
                ? null : startAt.plusDays(req.getDurationDays());

        String detail = req.getDetail();
        if (detail == null || detail.isBlank()) {
            detail = "provider=" + provider.getId() + ", place=" + req.getPlaceId();
        }

        return sanctionService.issueBlockContentByPlace(
                targetUserId,
                req.getPlaceId(),
                req.getReason(),
                detail,
                startAt,
                endAt,
                SanctionSource.MANUAL
        );
    }

    @Transactional
    public void unblockUser(Long providerId, Long targetUserId) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("제공자를 찾을 수 없습니다."));

        Place place = placeRepository.findByOwnerId(provider.getId())
                .orElseThrow(()-> new IllegalArgumentException("제공하는 장소가 없습니다."));

        sanctionService.revokeBlockContentByPlace(targetUserId, place.getId())
                .orElseThrow(() -> new IllegalArgumentException("활성화된 차단 내역이 없습니다."));
    }

}
