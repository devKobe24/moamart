package com.kobe.moamart.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.kobe.moamart.domain.product.entity
 * fileName       : ProductImage
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String url;

    private int displayOrder;

    private boolean isThumbnail;

    @Builder
    public ProductImage(String url, int displayOrder, boolean isThumbnail) {
        this.url = url;
        this.displayOrder = displayOrder;
        this.isThumbnail = isThumbnail;
    }

    // 연관관계 편의 메서드용 Setter (protected 권장하지만 패키지 레벨 접근 허용을 위해 public or default)
    public void assignProduct(Product product) {
        this.product = product;
    }
}
