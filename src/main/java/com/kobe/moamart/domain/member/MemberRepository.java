package com.kobe.moamart.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

/**
 * packageName    : com.kobe.moamart.domain.member
 * fileName       : MemberRepository
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
