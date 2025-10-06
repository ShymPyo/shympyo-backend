package shympyo.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shympyo.map.domain.PlaceType;
import shympyo.report.domain.Sanction;
import shympyo.report.domain.SanctionScope;
import shympyo.report.domain.SanctionType;
import shympyo.report.dto.ProviderBlockUserDetailResponse;
import shympyo.report.dto.ProviderBlockUserResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long> {

    @Query("""
        select s
        from Sanction s
        where s.targetUser.id = :userId
          and s.type = :type
          and s.scope = :scope
          and ((:scopeRefId is null and s.scopeRefId is null) or s.scopeRefId = :scopeRefId)
          and s.status = shympyo.report.domain.SanctionStatus.ACTIVE
          and s.startAt <= :now
          and (s.endAt is null or s.endAt > :now)
    """)
    Optional<Sanction> findActiveNow(Long userId,
                                     SanctionType type,
                                     SanctionScope scope,
                                     Long scopeRefId,
                                     LocalDateTime now);

    @Query("""
    select s from Sanction s
    where s.targetUser.id = :userId
      and s.type = :type
      and s.scope = :scope
      and s.scopeRefId = :placeId
      and s.status = shympyo.report.domain.SanctionStatus.ACTIVE
      and s.startAt <= :now
      and (s.endAt is null or s.endAt > :now)
    """)
    Optional<Sanction> findActiveNowByPlace(Long userId, SanctionType type, SanctionScope scope,
                                            Long placeId, LocalDateTime now);

    @Query("""
    select s from Sanction s
    where s.targetUser.id = :userId
      and s.type = :type
      and s.scope = :scope
      and s.scopeRefCategory = :category
      and s.status = shympyo.report.domain.SanctionStatus.ACTIVE
      and s.startAt <= :now
      and (s.endAt is null or s.endAt > :now)
    """)
    Optional<Sanction> findActiveNowByCategory(Long userId, SanctionType type, SanctionScope scope,
                                               PlaceType category, LocalDateTime now);

    @Query("""
        SELECT new shympyo.report.dto.ProviderBlockUserResponse(
            s.id, u.id, u.nickname, s.startAt, s.status
        )
        FROM Sanction s
        JOIN s.targetUser u
        WHERE s.type  = shympyo.report.domain.SanctionType.BLOCK_CONTENT
          AND s.scope = shympyo.report.domain.SanctionScope.PLACE_ONLY
          AND s.scopeRefId = :placeId
          AND s.status = shympyo.report.domain.SanctionStatus.ACTIVE
          AND s.startAt <= :now
          AND (s.endAt IS NULL OR s.endAt > :now)
        ORDER BY s.startAt DESC, s.id DESC
    """)
    List<ProviderBlockUserResponse> findActiveBlockedUsers(
            @Param("placeId") Long placeId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT new shympyo.report.dto.ProviderBlockUserDetailResponse(
            s.id, u.id, u.nickname, s.reason, s.detail, s.startAt, s.endAt, s.status
        )
        FROM Sanction s
        JOIN s.targetUser u
        WHERE s.type  = shympyo.report.domain.SanctionType.BLOCK_CONTENT
          AND s.scope = shympyo.report.domain.SanctionScope.PLACE_ONLY
          AND s.scopeRefId = :placeId
          AND u.id = :userId
          AND s.status = shympyo.report.domain.SanctionStatus.ACTIVE
          AND s.startAt <= :now
          AND (s.endAt IS NULL OR s.endAt > :now)
    """)
    Optional<ProviderBlockUserDetailResponse> findActiveBlockDetail(
            @Param("placeId") Long placeId,
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );


    @Query("""
        SELECT COUNT(s) > 0
        FROM Sanction s
        WHERE s.targetUser.id = :userId
          AND s.type = :type
          AND s.scope = :scope
          AND s.scopeRefCategory = :category
          AND s.status = 'ACTIVE'
          AND (s.endAt IS NULL OR s.endAt > :now)
    """)
    boolean existsActiveCategoryBlock(
            @Param("userId") Long userId,
            @Param("type") SanctionType type,
            @Param("scope") SanctionScope scope,
            @Param("category") PlaceType category,
            @Param("now") LocalDateTime now);


    @Query("""
        SELECT (COUNT(s) > 0)
        FROM Sanction s
        WHERE s.targetUser.id = :targetUserId
          AND s.type = :type
          AND s.scope = :scope
          AND s.scopeRefId = :placeId
          AND s.status = shympyo.report.domain.SanctionStatus.ACTIVE
          AND s.startAt <= :now
          AND (s.endAt IS NULL OR s.endAt > :now)
    """)
    boolean existsActivePlaceBlock(
            @Param("targetUserId") Long targetUserId,     // 차단당한 사용자 ID
            @Param("type") SanctionType type,             // BLOCK_CONTENT
            @Param("scope") SanctionScope scope,          // PLACE_ONLY
            @Param("placeId") Long placeId,               // 장소 ID
            @Param("now") LocalDateTime now
    );




}
