package com.kobe.moamart.domain.product.repository;

import com.kobe.moamart.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName    : com.kobe.moamart.domain.product.repository
 * fileName       : ProductRepository
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */

// JpaRepository의 기본 기능 + 내가 만든 검색 기능(Custom)을 모두 상속
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    // 필요한 경우 간단한 메서드는 여기에 추가 (예: 상품명으로 정확히 찾기)

    // 메인 페이지용: 노출(isDisplayed)이 true이고 최신 상품(isNew)인 상품만 최신순으로 가져오기
    Page<Product> findAllByIsDisplayedTrueAndIsNewTrueOrderByIdDesc(Pageable pageable);

    // 메인 페이지용: 특정 카테고리의 노출 상품 중 최신 상품만 조회
    Page<Product> findByCategoryIdAndIsDisplayedTrueAndIsNewTrueOrderByIdDesc(Long categoryId, Pageable pageable);

    // 메인 페이지용: 노출(isDisplayed)이 true인 모든 상품을 최신순으로 가져오기
    Page<Product> findAllByIsDisplayedTrueOrderByIdDesc(Pageable pageable);

    // 메인 페이지용: 특정 카테고리의 노출 상품만 조회
    Page<Product> findByCategoryIdAndIsDisplayedTrueOrderByIdDesc(Long categoryId, Pageable pageable);

    // 상품명으로 상품 찾기 (교환 시 사용)
    java.util.Optional<Product> findByName(String name);
}
