package com.kobe.moamart.domain.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.kobe.moamart.domain.product.entity
 * fileName       : ProductStatus
 * author         : kobe
 * date           : 2025. 12. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 25.        kobe       최초 생성
 */
@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    SELL("판매중"),
    SOLD_OUT("품절"),
    STOP("판매중지");

    private final String description;
}
