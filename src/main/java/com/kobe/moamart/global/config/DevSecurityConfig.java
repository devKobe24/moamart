package com.kobe.moamart.global.config;

import com.kobe.moamart.global.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * packageName    : com.kobe.moamart.global.config
 * fileName       : DevSecurityConfig
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Configuration
@EnableWebSecurity
@Profile("dev") // application.yml의 active: dev 일 때만 동작
@RequiredArgsConstructor
public class DevSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (개발 편의성)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. H2 Console 허용 (iframe)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 3. 권한 제어
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/h2-console/**").permitAll()
                        // 로그인 및 회원가입 페이지는 누구나 접근 가능
                        .requestMatchers("/admin/login", "/join", "/login").permitAll()
                        // 메인 페이지 및 상품 상세는 누구나 접근 가능
                        .requestMatchers("/", "/products/**", "/cart", "/orders/**").permitAll()
                        // 관리자 페이지는 ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 4. 로그인 폼 설정
                .formLogin(login -> login
                        .loginPage("/login") // 기본 로그인 페이지 (일반 사용자)
                        .loginProcessingUrl("/login")
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws jakarta.servlet.ServletException, java.io.IOException {
                                // 권한에 따라 리다이렉트 처리
                                boolean isAdmin = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                                if (isAdmin) {
                                    response.sendRedirect("/admin/products");
                                } else {
                                    response.sendRedirect("/");
                                }
                            }
                        })
                        .failureUrl("/login?error=true") // 로그인 실패 시
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // 5. UserDetailsService 설정
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
