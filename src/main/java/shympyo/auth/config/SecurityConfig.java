package shympyo.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import shympyo.auth.jwt.JwtAuthenticationFilter;
import shympyo.auth.jwt.JwtTokenProvider;
import shympyo.auth.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/", "/error",

                                // 스웨거
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",

                                // 헬스 체크
                                "/actuator/health",
                                "/actuator/health/**",

                                // 날씨 API 추가!
                                "/api/weather/**",

                                // 공개 API
                                "/api/auth/**",
                                "/api/users/signup",
                                "/api/users/oauth",
                                "/oauth/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:*}") String originsCsv) {
        var cfg = new CorsConfiguration();
        for (String o : originsCsv.split(",")) cfg.addAllowedOriginPattern(o.trim());
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        cfg.setAllowCredentials(true);
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public OncePerRequestFilter reqLog() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest r, HttpServletResponse s, FilterChain c)
                    throws java.io.IOException, jakarta.servlet.ServletException {
                long t0 = System.currentTimeMillis();
                String uri = r.getRequestURI();
                String method = r.getMethod();
                String ip = r.getRemoteAddr();
                try {
                    c.doFilter(r, s);
                } finally {
                    int status = s.getStatus();
                    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                    String user = (auth == null) ? "anonymous" : auth.getName();
                    String roles = (auth == null) ? "-" :
                            auth.getAuthorities().toString();
                    long ms = System.currentTimeMillis() - t0;
                    System.out.printf("[REQ] %s %s %dms ip=%s user=%s roles=%s status=%d%n",
                            method, uri, ms, ip, user, roles, status);
                }
            }
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
