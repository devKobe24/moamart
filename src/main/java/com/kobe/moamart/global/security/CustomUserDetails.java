package com.kobe.moamart.global.security;

import com.kobe.moamart.domain.admin.Admin;
import com.kobe.moamart.domain.member.Member;
import com.kobe.moamart.domain.role.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * packageName    : com.kobe.moamart.global.security
 * fileName       : CustomUserDetails
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    : Spring Security UserDetails 구현체
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final Admin admin;

    public CustomUserDetails(Member member) {
        this.member = member;
        this.admin = null;
    }

    public CustomUserDetails(Admin admin) {
        this.member = null;
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = member != null ? member.getRole() : admin.getRole();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return member != null ? member.getPassword() : admin.getPassword();
    }

    @Override
    public String getUsername() {
        // Member는 email, Admin은 username을 반환
        return member != null ? member.getEmail() : admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

