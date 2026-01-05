package com.kobe.moamart.controller.view;

import com.kobe.moamart.domain.category.CategoryRepository;
import com.kobe.moamart.dto.response.ProductDetailResponse;
import com.kobe.moamart.dto.response.ProductListResponse;
import com.kobe.moamart.service.CartService;
import com.kobe.moamart.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


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
    private final CartService cartService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 12) Pageable pageable,
            HttpSession session,
            Model model
    ) {
        // 1. 카테고리 이름으로 카테고리 ID 조회
        Long categoryId = null;
        if (category != null && !category.isEmpty()) {
            categoryId = categoryRepository.findByName(category)
                    .map(cat -> cat.getId())
                    .orElse(null);
        }

        // 2. 최신 상품 조회 (isNew=true인 상품만)
        Page<ProductListResponse> newProducts = productService.getNewProducts(categoryId, pageable);

        // 3. 전체 상품 조회 (isDisplayed=true인 모든 상품)
        Page<ProductListResponse> allProducts = productService.getAllProducts(categoryId, pageable);

        // 4. 장바구니 아이템 수 조회
        int cartItemCount = cartService.getCartFromSession(session).size();

        // 5. 모델에 담기
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("allProducts", allProducts);
        model.addAttribute("selectedCategory", category); // 선택된 카테고리 전달 (활성화 표시용)
        model.addAttribute("cartItemCount", cartItemCount); // 장바구니 아이템 수

        return "index"; // templates/index.html
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        ProductDetailResponse product = productService.getProductDetail(id);
        model.addAttribute("product", product);
        return "product/detail"; // templates/product/detail.html
    }
}
