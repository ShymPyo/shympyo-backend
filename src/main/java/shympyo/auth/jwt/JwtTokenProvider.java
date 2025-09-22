package shympyo.auth.jwt;

import shympyo.user.domain.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Duration accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Duration refreshExpiration;

    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, UserRole role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpiration.toMillis());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpiration.toMillis());
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try { Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    public Long getUserId(String token) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return Long.parseLong(c.getSubject());
    }

    public String getRole(String token) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return c.get("role", String.class);
    }
}
