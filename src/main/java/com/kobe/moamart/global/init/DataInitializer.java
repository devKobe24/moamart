package com.kobe.moamart.global.init;

import com.kobe.moamart.domain.category.Category;
import com.kobe.moamart.domain.category.CategoryRepository;
import com.kobe.moamart.domain.member.Member;
import com.kobe.moamart.domain.member.MemberRepository;
import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.kobe.moamart.global.init
 * fileName       : DataInitializer
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 이미 데이터가 있으면 초기화하지 않음 (중복 방지)
        if (categoryRepository.count() > 0) {
            return;
        }

        System.out.println("🚀 [DataInitializer] 초기 데이터 생성을 시작합니다...");

        // 2. 관리자 계정 생성
        initAdmin();

        // 3. 카테고리 생성 & 상품 등록
        initCategoryAndProduct();

        System.out.println("✅ [DataInitializer] 초기 데이터 생성 완료!");
    }

    private void initAdmin() {
        Member admin = Member.builder()
                .username("admin")
                .password("{noop}1234") // {noop}: 암호화 없이 텍스트 그대로 로그인 (테스트용)
                .role("ROLE_ADMIN")
                .build();
        memberRepository.save(admin);
    }

    private void initCategoryAndProduct() {
        // --- 대분류 카테고리 생성 (텍스트 파일 기준) ---
        String[] categoryNames = {
            "주류", "정육", "채소", "빵 • 잼", "우유 • 유제품",
            "아이스크림", "음료 • 커피 • 생수", "라면 • 면", "세제 • 방향 • 탈취",
            "청소 • 욕실", "즉석 밥", "쌀 • 잡곡 • 견과", "햄 • 어묵 • 통조림",
            "양념 • 장류 • 오일", "요리 • 반찬", "헤어 • 바디 • 쉐이빙",
            "화장지 • 생리대", "주방 • 일회용품", "뷰티 • 클렌징", "문구 • 사무용품"
        };

        for (int i = 0; i < categoryNames.length; i++) {
            Category category = createCategory(categoryNames[i], 1, i + 1, null);
            categoryRepository.save(category);
        }

        // 기존 상품 데이터는 제거 (필요시 나중에 추가)
    }

    private Category createCategory(String name, int depth, int order, Category parent) {
        return Category.builder()
                .name(name)
                .depth(depth)
                .displayOrder(order)
                .parent(parent)
                .build();
    }

    private void createProduct(Category category, String name, Long price, String description, Boolean isDisplayed, ProductStatus status) {
        Product product = Product.builder()
                .category(category)
                .name(name)
                .price(price)
                .description(description)
                .status(status)
                .isDisplayed(isDisplayed)
                .thumbnailUrl(null)
                .stockQuantity(100)
                .build();

        productRepository.save(product);
    }
}
