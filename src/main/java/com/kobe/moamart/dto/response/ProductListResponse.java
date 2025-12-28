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

    // Entity -> DTO 변환 생성자
    public ProductListResponse(Product product) {
        this.id = product.getId();
        this.categoryName = product.getCategory().getName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.isDisplayed = product.isDisplayed();
        this.updatedAt = product.getUpdatedAt();
        this.thumbnailUrl = product.getThumbnailUrl();
    }
}
