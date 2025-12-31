package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * packageName    : com.kobe.moamart.dto.response
 * fileName       : ProductListResponse
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Getter
public class ProductListResponse {
    private Long id;
    private String categoryName;
    private String name;
    private Long price;
    private ProductStatus status; // ENUM (SELL, SOLD_OUT,...)
    private boolean isDisplayed;
    private LocalDateTime updatedAt;
    private String thumbnailUrl;
    private int stockQuantity; // 남은 수량 (현재 재고 수량)
    private int initialStockQuantity; // 입고량 (처음 입고된 수량)

    // Entity -> DTO 변환 생성자 (주문 수량 포함)
    public ProductListResponse(Product product, int totalOrderedQuantity) {
        this.id = product.getId();
        this.categoryName = product.getCategory().getName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.isDisplayed = product.isDisplayed();
        this.updatedAt = product.getUpdatedAt();
        this.thumbnailUrl = product.getThumbnailUrl();
        this.stockQuantity = product.getStockQuantity(); // 남은 수량
        this.initialStockQuantity = product.getStockQuantity() + totalOrderedQuantity; // 입고량 = 남은 수량 + 주문된 수량
    }

    // Entity -> DTO 변환 생성자 (기본 생성자 - 주문 수량 없이 호출 시)
    public ProductListResponse(Product product) {
        this(product, 0); // 주문 수량을 모르면 0으로 설정
    }
}
