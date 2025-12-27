package com.kobe.moamart.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.kobe.moamart.domain.member
 * fileName       : Role
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;
}
