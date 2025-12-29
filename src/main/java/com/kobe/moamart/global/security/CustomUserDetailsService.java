package com.kobe.moamart.global.security;

import com.kobe.moamart.domain.admin.Admin;
import com.kobe.moamart.domain.admin.AdminRepository;
import com.kobe.moamart.domain.member.Member;
import com.kobe.moamart.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.kobe.moamart.global.security
 * fileName       : CustomUserDetailsService
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    : Spring Security UserDetailsService 구현체
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Admin 먼저 조회 (username으로)
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            return new CustomUserDetails(admin);
        }

        // 2. Member 조회 (email로)
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return new CustomUserDetails(member);
    }
}

