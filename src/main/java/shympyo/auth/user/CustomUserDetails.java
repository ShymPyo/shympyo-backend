package shympyo.auth.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import shympyo.user.domain.UserRole;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final UserRole role;

    public CustomUserDetails(Long id, UserRole role) {
        this.id = id;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Security에서 인가 걸고 싶으면 ROLE_ 접두사 붙여주는 게 일반적
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return null; // JWT 기반 인증이라 비밀번호 의미 없음
    }

    @Override
    public String getUsername() {
        return String.valueOf(id); // 굳이 이메일 안 쓰면 그냥 id 문자열 리턴
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
