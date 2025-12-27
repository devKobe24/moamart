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
        // --- 대분류 ---
        Category dairyProducts = createCategory("유제품", 1, 1, null);
        Category snack = createCategory("과자", 1, 1, null);
        Category cannedFood = createCategory("통조림", 1, 1, null);

        categoryRepository.save(dairyProducts);
        categoryRepository.save(snack);
        categoryRepository.save(cannedFood);

        // --- 소분류 (유제품) ---
        Category milk = createCategory("우유", 2, 1, dairyProducts);
        Category cheese = createCategory("치즈", 2, 2, dairyProducts);
        categoryRepository.save(milk);
        categoryRepository.save(cheese);

        // --- 소분류 (패션) ---
        Category shrimpSnack = createCategory("새우맛 과자", 2, 1, snack);
        Category potatoChips = createCategory("감자칩", 2, 2, snack);
        categoryRepository.save(shrimpSnack);
        categoryRepository.save(potatoChips);

        // --- 상품 데이터 (Dummy) ---
        createProduct(milk, "서울 유우 500ML", 1900L, "1등급 받아 고소한 흰 우유",true ,ProductStatus.SELL);
        createProduct(milk, "덴마크 소화가 잘되는 우유 900ml", 3880L, "1A등급 전용목장 원유로 만든 소화가 잘되는 우유", true, ProductStatus.SELL);
        createProduct(cheese, "서울우유 체다치즈", 4870L, "한국인이 사랑하는 맛", true ,ProductStatus.SOLD_OUT);
        createProduct(shrimpSnack, "매운 새우깡", 1180L, "생새우로 만든 매운 새우깡", true, ProductStatus.SELL);
        createProduct(potatoChips, "프링글스 캐리비안식 치킨맛 100g", 3330L, "로컬의 맛을 통째로 쌓아 올렸닭", true, ProductStatus.STOP); // 판매중지 테스트
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
                .build();

        productRepository.save(product);
    }
}
