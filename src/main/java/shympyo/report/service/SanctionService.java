package shympyo.report.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.map.domain.PlaceType;
import shympyo.report.domain.*;
import shympyo.report.repository.SanctionRepository;
import shympyo.user.domain.User;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SanctionService {

    private final SanctionRepository sanctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long issueSuspend(Long targetUserId,
                             SanctionScope scope,
                             Long scopeRefId,
                             SanctionType type,
                             SanctionReason reason,
                             String detail,
                             LocalDateTime startAt,
                             LocalDateTime endAt,
                             SanctionSource source) {

        if (type != SanctionType.SUSPEND) {
            throw new IllegalArgumentException("issueSuspend는 SanctionType.SUSPEND만 허용합니다.");
        }
        if (targetUserId == null) {
            throw new IllegalArgumentException("targetUserId는 필수입니다.");
        }
        if (startAt == null) startAt = LocalDateTime.now();
        if (endAt != null && endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("제재 종료 시각이 시작 시각보다 빠릅니다.");
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("제재 대상 사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();


        final LocalDateTime start = (startAt == null) ? LocalDateTime.now() : startAt;
        final LocalDateTime end = endAt;


        return sanctionRepository.findActiveNow(targetUserId, type, scope, scopeRefId, now)
                .map(active -> {
                    active.extendUntil(endAt, "[EXTEND] " + (detail == null ? "" : detail));
                    return active.getId();
                })
                .orElseGet(() -> {

                    Sanction created = Sanction.builder()
                            .targetUser(target)
                            .type(type)
                            .scope(scope)
                            .scopeRefId(scopeRefId)
                            .reason(reason)
                            .detail(detail)
                            .startAt(start)
                            .endAt(end)
                            .status(SanctionStatus.ACTIVE)
                            .source(source)
                            .build();

                    return sanctionRepository.save(created).getId();
                });
    }

    @Transactional
    public Long issueBlockContentByPlace(Long targetUserId,
                                         Long placeId,
                                         SanctionReason reason,
                                         String detail,
                                         LocalDateTime startAt,
                                         LocalDateTime endAt,
                                         SanctionSource source) {
        if (targetUserId == null || placeId == null)
            throw new IllegalArgumentException("targetUserId/placeId는 필수입니다.");
        if (startAt == null) startAt = LocalDateTime.now();
        if (endAt != null && endAt.isBefore(startAt))
            throw new IllegalArgumentException("종료가 시작보다 빠릅니다.");

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("제재 대상 사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();

        final LocalDateTime start = startAt;
        final LocalDateTime end = endAt;
        final Long refPlaceId = placeId;

        return sanctionRepository.findActiveNowByPlace(
                        targetUserId, SanctionType.BLOCK_CONTENT, SanctionScope.PLACE_ONLY, refPlaceId, now)
                .map(s -> {
                    s.extendUntil(end, "[EXTEND] " + (detail == null ? "" : detail));
                    return s.getId();
                })
                .orElseGet(() -> sanctionRepository.save(
                        Sanction.builder()
                                .targetUser(target)
                                .type(SanctionType.BLOCK_CONTENT)
                                .scope(SanctionScope.PLACE_ONLY)
                                .scopeRefId(refPlaceId)
                                .reason(reason)
                                .detail(detail)
                                .startAt(start)
                                .endAt(end)
                                .status(SanctionStatus.ACTIVE)
                                .source(source)
                                .build()
                ).getId());
    }


    @Transactional
    public Long issueBlockContentByCategory(Long targetUserId,
                                            PlaceType category,
                                            SanctionReason reason,
                                            String detail,
                                            LocalDateTime startAt,
                                            LocalDateTime endAt,
                                            SanctionSource source) {
        if (targetUserId == null || category == null)
            throw new IllegalArgumentException("targetUserId/category는 필수입니다.");
        if (startAt == null) startAt = LocalDateTime.now();
        if (endAt != null && endAt.isBefore(startAt))
            throw new IllegalArgumentException("종료가 시작보다 빠릅니다.");

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("제재 대상 사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();

        final LocalDateTime start = (startAt == null) ? LocalDateTime.now() : startAt;
        final LocalDateTime end = endAt;

        return sanctionRepository.findActiveNowByCategory(targetUserId, SanctionType.BLOCK_CONTENT,
                        SanctionScope.PLACE_CATEGORY, category, now)
                .map(s -> { s.extendUntil(endAt, "[EXTEND] " + (detail == null ? "" : detail)); return s.getId(); })
                .orElseGet(() -> sanctionRepository.save(
                        Sanction.builder()
                                .targetUser(target)
                                .type(SanctionType.BLOCK_CONTENT)
                                .scope(SanctionScope.PLACE_CATEGORY)
                                .scopeRefCategory(category)
                                .reason(reason)
                                .detail(detail)
                                .startAt(start)
                                .endAt(end)
                                .status(SanctionStatus.ACTIVE)
                                .source(source)
                                .build()
                ).getId());
    }


    @Transactional
    public Optional<Long> revokeBlockContentByPlace(Long targetUserId, Long placeId) {
        LocalDateTime now = LocalDateTime.now();

        return sanctionRepository.findActiveNowByPlace(
                targetUserId,
                SanctionType.BLOCK_CONTENT,
                SanctionScope.PLACE_ONLY,
                placeId,
                now
        ).map(s -> {
            s.revoke("제공자 요청으로 장소 차단 해제");
            return s.getId();
        });
    }

}
