package shympyo.auth.repository;

import shympyo.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    boolean existsByToken(String token);
}
