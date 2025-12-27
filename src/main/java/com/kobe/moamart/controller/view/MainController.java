package com.kobe.moamart.controller.view;

import com.kobe.moamart.dto.response.ProductListResponse;
import com.kobe.moamart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



/**
 * packageName    : com.kobe.moamart.controller.view
 * fileName       : MainController
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(@PageableDefault(size = 12) Pageable pageable, Model model) {
        // 1. 노출 가능한 상품 조회
        Page<ProductListResponse> products = productService.getMainPageProducts(pageable);

        // 2. 모델에 담기
        model.addAttribute("products", products);

        return "index"; // templates/index.html
    }
}
