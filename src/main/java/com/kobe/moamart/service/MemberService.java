package com.kobe.moamart.service;

import com.kobe.moamart.domain.member.Member;
import com.kobe.moamart.domain.member.MemberRepository;
import com.kobe.moamart.domain.role.Role;
import com.kobe.moamart.dto.request.MemberJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.kobe.moamart.service
 * fileName       : MemberService
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(MemberJoinRequest request) {
        // 1. 중복 회원 검증
        validateDuplicateMember(request.getEmail());

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 회원 저장 (기본 권한: USER)
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .address(request.getAddress())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 가입된 이메일입니다.");
                });
    }
}
