package com.kobe.moamart.controller.view;

import com.kobe.moamart.dto.request.MemberJoinRequest;
import com.kobe.moamart.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * packageName    : com.kobe.moamart.controller.view
 * fileName       : MemberController
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 페이지
    @GetMapping("/join")
    public String joinForm() {
        return "member/join"; // templates/member/join.html
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String join(MemberJoinRequest request, Model model) {
        try {
            memberService.join(request);
            return "redirect:/"; // 성공 시 홈으로
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join"; // 실패 시 다시 가입 페이지로
        }
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "member/login"; // templates/member/login.html
    }
}
