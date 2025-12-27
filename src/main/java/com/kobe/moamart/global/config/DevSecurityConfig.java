package com.kobe.moamart.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (개발 편의성)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. H2 Console 허용 (iframe)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 3. 모든 요청 허용 (로그인 없이 개발 가능)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                );
        return http.build();
    }
}
