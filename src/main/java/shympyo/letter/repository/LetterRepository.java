package shympyo.letter.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shympyo.letter.domain.Letter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    boolean existsByRentalId(Long rentalId);

    Optional<Letter> findByRentalId(Long rentalId);

    @Query("""
      select l from Letter l
      join fetch l.writer w
      join fetch l.rental r
      join fetch r.place p
      join fetch p.owner o
      where l.id = :id
    """)
    Optional<Letter> findDetailById(@Param("id") Long id);

    @Query("""
        select l from Letter l
        join l.rental r
        join r.place p
        where p.owner.id = :ownerId
        order by l.createdAt desc
    """)
    List<Letter> findAllByOwner(@Param("ownerId") Long ownerId);

    @Query("""
        SELECT l
        FROM Letter l
        JOIN l.rental r
        JOIN r.place p
        WHERE p.owner.id = :ownerId
        ORDER BY l.createdAt DESC, l.id DESC
    """)
    Slice<Letter> findReceivedByOwner(@Param("ownerId") Long ownerId,
                                      Pageable pageable);

    @Query("""
      SELECT l
      FROM Letter l
      JOIN l.rental r
      JOIN r.place p
      WHERE p.owner.id = :ownerId
        AND (
             l.createdAt < :cursorCreatedAt
          OR (l.createdAt = :cursorCreatedAt AND l.id < :cursorId)
        )
      ORDER BY l.createdAt DESC, l.id DESC
    """)
    Slice<Letter> findReceivedByOwnerWithCursor(@Param("ownerId") Long ownerId,
                                                @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
                                                @Param("cursorId") Long cursorId,
                                                Pageable pageable);

    @Query("""
        select count(l) from Letter l
        join l.rental r
        join r.place p
        where p.owner.id = :ownerId
    """)
    Long countAllByOwner(@Param("ownerId") Long ownerId);

    @Query("""
        select count(l) from Letter l
        join l.rental r
        join r.place p
        where p.owner.id = :ownerId and l.isRead = false
    """)
    Long countUnreadByOwner(@Param("ownerId") Long ownerId);

}
