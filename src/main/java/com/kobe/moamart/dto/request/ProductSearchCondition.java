package com.kobe.moamart.dto.request;

import com.kobe.moamart.domain.product.entity.ProductStatus;
import lombok.Data;

/**
 * packageName    : com.kobe.moamart.dto.request
 * fileName       : ProductSearchCondition
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Data
public class ProductSearchCondition {
    private Long categoryId; // 특정 카테고리 필터링
    private String keyword; // 상품명 검생
    private ProductStatus status; // 판매 상태(판매중/품절) 필터

    // 관리자용: 삭제된 상품 포함 여부등을 추가할 수도 있음
}
