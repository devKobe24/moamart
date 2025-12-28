package com.kobe.moamart.controller.view.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * packageName    : com.kobe.moamart.controller.view.admin
 * fileName       : AdminLoginController
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    : 관리자 로그인 페이지 컨트롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login"; // templates/admin/login.html
    }
}

