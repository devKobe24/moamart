package com.kobe.moamart.controller.view.admin;

import com.kobe.moamart.domain.category.CategoryRepository;
import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import com.kobe.moamart.dto.request.ProductSaveRequest;
import com.kobe.moamart.dto.request.ProductSearchCondition;
import com.kobe.moamart.dto.response.ProductListResponse;
import com.kobe.moamart.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * packageName    : com.kobe.moamart.controller.view.admin
 * fileName       : AdminProductViewController
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductViewController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository; // 폼에 카테고리 목록 뿌려줘야 함

    @GetMapping
    public String list(
            @ModelAttribute ProductSearchCondition condition, // 검색 조건 (쿼리파리미터 바인딩)
            @PageableDefault(size = 10)Pageable pageable, // 페이징 (기본 10개)
            Model model
    ) {
        Page<ProductListResponse> result = productService.getAdminProductList(condition, pageable);

        model.addAttribute("products", result);
        model.addAttribute("condition", condition); // 검색 조건을 뷰에 다시 전달 (검색창 유지용)

        return "admin/product/list"; // templates/admin/product/list.html
    }

    /**
     * 상품 등록 폼 화면 이동
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        // 1. 빈 객체를 넘겨줘야 Thymeleaf가 필드를 바인딩함
        model.addAttribute("product", new ProductSaveRequest());

        // 2. 카테고리 선택 셀렉트박스를 위해 전테 카테고리 전달
        model.addAttribute("categories", categoryRepository.findAll());

        // 3. 상태 Enum 전달
        model.addAttribute("statuses", ProductStatus.values());

        return "admin/product/form"; // templates/admin/product/form.html
    }

    /**
     * 상품 등록 처리
     */
    @PostMapping("/add")
    public String saveProduct(
            @Valid
            @ModelAttribute("product") ProductSaveRequest request,
            BindingResult bindingResult,
            Model model
    ) {
       // 유효성 검사 실패 시 다시 품으로 이동
       if (bindingResult.hasErrors()) {
           model.addAttribute("categories", categoryRepository.findAll());
           model.addAttribute("statuses", ProductStatus.values());
           return "admin/product/form";
       }

       // 저장 로직 수행
        productService.saveProduct(request);

       // 목록 페이지로 리다이렉트 (PRG 패턴)
        return "redirect:/admin/products";
    }
}
