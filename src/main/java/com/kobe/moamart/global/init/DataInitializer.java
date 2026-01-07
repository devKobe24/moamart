package com.kobe.moamart.global.init;

import com.kobe.moamart.domain.admin.Admin;
import com.kobe.moamart.domain.admin.AdminRepository;
import com.kobe.moamart.domain.category.Category;
import com.kobe.moamart.domain.category.CategoryRepository;
import com.kobe.moamart.domain.member.MemberRepository;
import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import com.kobe.moamart.domain.role.Role;
import com.kobe.moamart.domain.store.entity.Store;
import com.kobe.moamart.domain.store.repository.StoreRepository;
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
    private final AdminRepository adminRepository;
    private final StoreRepository storeRepository;

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

        // 3. 기본 매장 생성
        initStore();

        // 4. 카테고리 생성 & 상품 등록
        initCategoryAndProduct();

        System.out.println("✅ [DataInitializer] 초기 데이터 생성 완료!");
    }

    private void initAdmin() {
        Admin admin = Admin.builder()
                .username("admin")
                .password("{noop}1234") // {noop}: 암호화 없이 텍스트 그대로 로그인 (테스트용)
                .role(Role.ADMIN)
                .build();
        adminRepository.save(admin);
    }

    private void initStore() {
        // 기본 매장이 없으면 생성
        if (storeRepository.findByName("모아마트").isEmpty()) {
            Store defaultStore = Store.builder()
                    .name("모아마트")
                    .address("대전광역시 대덕구 중리로31번길 47")
                    .phoneNumber("042-522-4462")
                    .isActive(true)
                    .build();
            storeRepository.save(defaultStore);
        }
    }

    private void initCategoryAndProduct() {
        // --- 대분류 카테고리 생성 ---
        Category freshFood = createCategory("신선 식품", 1, 1, null);
        Category processedFood = createCategory("가공 식품", 1, 2, null);
        Category condimentsAndIngredients = createCategory("조미료 • 식재료", 1, 3, null);
        Category beverages = createCategory("음료", 1, 4, null);
        Category alcohol = createCategory("주류", 1, 5, null);
        Category householdGoods = createCategory("생활용품", 1, 6, null);
        Category beautyAndHygiene = createCategory("뷰티 • 위생", 1, 7, null);
        Category stationeryAndOfficeSupplies = createCategory("문구 • 사무용품", 1, 8, null);
        Category others = createCategory("기타", 1, 9, null);

        categoryRepository.save(freshFood);
        categoryRepository.save(processedFood);
        categoryRepository.save(condimentsAndIngredients);
        categoryRepository.save(beverages);
        categoryRepository.save(alcohol);
        categoryRepository.save(householdGoods);
        categoryRepository.save(beautyAndHygiene);
        categoryRepository.save(stationeryAndOfficeSupplies);
        categoryRepository.save(others);

        // --- 소분류 카테고리 생성 ---
        // 신선 식품의 소분류
        categoryRepository.save(createCategory("정육", 2, 1, freshFood));
        categoryRepository.save(createCategory("채소", 2, 2, freshFood));
        categoryRepository.save(createCategory("우유 • 유제품", 2, 3, freshFood));

        // 가공 식품의 소분류
        categoryRepository.save(createCategory("라면 • 면", 2, 1, processedFood));
        categoryRepository.save(createCategory("햄 • 어묵 • 통조림", 2, 2, processedFood));
        categoryRepository.save(createCategory("아이스크림", 2, 3, processedFood));
        categoryRepository.save(createCategory("빵 • 잼", 2, 4, processedFood));

        // 조미료 • 식재료의 소분류
        categoryRepository.save(createCategory("요리 • 반찬", 2, 1, condimentsAndIngredients));
        categoryRepository.save(createCategory("양념 • 장류 • 오일", 2, 2, condimentsAndIngredients));
        categoryRepository.save(createCategory("쌀 • 잡곡 • 견과", 2, 3, condimentsAndIngredients));

        // 음료의 소분류
        categoryRepository.save(createCategory("음료 • 커피 • 생수", 2, 1, beverages));

        // 주류의 소분류
        categoryRepository.save(createCategory("맥주",2, 1, alcohol));
        categoryRepository.save(createCategory("소주",2, 2, alcohol));
        categoryRepository.save(createCategory("막걸리",2, 3, alcohol));

        // 생활용품의 소분류
        categoryRepository.save(createCategory("세제 • 방향 • 탈취", 2, 1, householdGoods));
        categoryRepository.save(createCategory("청소 • 욕실", 2, 2, householdGoods));
        categoryRepository.save(createCategory("주방 • 일회용품", 2, 3, householdGoods));
        categoryRepository.save(createCategory("화장지 • 생리대", 2, 4, householdGoods));

        // 뷰티 • 위생의 소분류
        categoryRepository.save(createCategory("헤어 • 바디 • 쉐이빙", 2, 1, beautyAndHygiene));
        categoryRepository.save(createCategory("뷰티 • 클렌징", 2, 2, beautyAndHygiene));

        // 문구 • 사무용품의 소분류
        categoryRepository.save(createCategory("문구 • 사무용품", 2, 1, stationeryAndOfficeSupplies));

        // 기타의 소분류
        categoryRepository.save(createCategory("기타 용품", 2, 1, others));

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
                .isNew(false) // 기본값은 false
                .thumbnailUrl(null)
                .stockQuantity(100)
                .build();

        productRepository.save(product);
    }
}
