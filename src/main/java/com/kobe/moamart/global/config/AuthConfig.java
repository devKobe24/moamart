package com.kobe.moamart.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.kobe.moamart.global.config
 * fileName       : AuthConfig
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Configuration
public class AuthConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, bcryptEncoder);
        // {noop} prefix를 지원하기 위한 설정 (테스트용, deprecated이지만 호환성을 위해 사용)
        @SuppressWarnings("deprecation")
        PasswordEncoder noOpEncoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        encoders.put("noop", noOpEncoder);

        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        // 기본 인코딩이 없는 경우 bcrypt를 사용하도록 설정
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptEncoder);
        return delegatingPasswordEncoder;
    }
}
