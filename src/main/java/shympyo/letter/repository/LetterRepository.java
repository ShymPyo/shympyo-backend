package shympyo.letter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shympyo.letter.domain.Letter;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    // 장소 주인 받은 모든 편지 수
    @Query("""
        select count(l)
        from Letter l
        where l.place.owner.id = :ownerId
    """)
    long countAllByOwner(@Param("ownerId") Long ownerId);

    // 장소 주인이 받은 편지 중 안 읽은 것 개수
    @Query("""
      select count(l) from Letter l
      where l.place.owner.id = :ownerId and l.isRead = false
    """)
    long countUnreadByOwner(@Param("ownerId") Long ownerId);

    // 받은 편지 목록 (최신순)
    @Query("""
      select l from Letter l
      where l.place.owner.id = :ownerId
      order by l.createdAt desc
    """)
    List<Letter> findAllByOwner(@Param("ownerId") Long ownerId);

}
