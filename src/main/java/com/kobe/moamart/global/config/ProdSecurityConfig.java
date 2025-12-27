package com.kobe.moamart.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * packageName    : com.kobe.moamart.global.config
 * fileName       : ProdSecurityConfig
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
@Profile("prod")
public class ProdSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 활성화 (기본값이라 명시 안 해도 되지만 명시적 선언)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // API는 토큰 방식이면 끄기도 함

                // 2. H2 Console은 접근 불가 (Prod에는 H2 자체를 안 쓰지만 확실히 차단)

                // 3. 권한 제어 (Whitelist 방식)
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 허용
                        .requestMatchers("/css/**", "js/**", "/images/**", "/favicon.ico").permitAll()
                        // 메인 페이지 및 상품 상세는 누구나 접근 가능
                        .requestMatchers("/", "/products/**").permitAll()
                        // 관리자 페이지는 ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 4. 로그인 폼 설정
                .formLogin(login -> login
                        .loginPage("/admin/login") // 커스텀 로그인 페이지
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}
