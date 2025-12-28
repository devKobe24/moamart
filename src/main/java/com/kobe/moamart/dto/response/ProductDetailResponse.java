package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.kobe.moamart.dto.response
 * fileName       : ProductDetailResponse
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */
@Getter
public class ProductDetailResponse {
    private Long id;
    private String categoryName;
    private String name;
    private Long price;
    private ProductStatus status;
    private String description;
    private String thumbnailUrl;
    private List<String> detailImages; // 상세 이미지 URL 리스트

    public ProductDetailResponse(Product product) {
        this.id = product.getId();
        this.categoryName = product.getCategory().getName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.description = product.getDescription();
        this.thumbnailUrl = product.getThumbnailUrl();

        // ProductImage 엔티티 리스트를 URL 문자열 리스트로 변환
        this.detailImages = product.getImages().stream()
                .map(image -> image.getUrl())
                .collect(Collectors.toList());
    }
}
