package com.kobe.moamart.domain.product.repository;

import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.dto.request.ProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * packageName    : com.kobe.moamart.domain.product.repository
 * fileName       : ProductRepositoryCustom
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
public interface ProductRepositoryCustom {
    // 동적 검색 + 페이징
    Page<Product> search(ProductSearchCondition condition, Pageable pageable);
}
