package com.kobe.moamart.global.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * packageName    : com.kobe.moamart.global.util
 * fileName       : PasswordGenerator
 * author         : kobe
 * date           : 2026. 1. 6.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 6.        kobe       최초 생성
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "";
        String encodedPassword = encoder.encode(password);
        System.out.println("{bcrypt}" + encodedPassword);
    }
}
