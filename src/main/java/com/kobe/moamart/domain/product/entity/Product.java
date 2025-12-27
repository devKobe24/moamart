package com.kobe.moamart.domain.product.entity;

import com.kobe.moamart.domain.BaseTimeEntity;
import com.kobe.moamart.domain.category.Category;
import com.kobe.moamart.global.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.kobe.moamart.domain.product.entity
 * fileName       : Product
 * author         : kobe
 * date           : 2025. 12. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 25.        kobe       최초 생성
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 목록 조회 성능 최적화를 위한 썸네일 URL (반정규화)
    private String thumbnailUrl;

    @Column(nullable = false)
    private boolean isDisplayed;

    // 재고 수량 필드 (기본값 0)
    private int stockQuantity;

    // CascadeType.ALL: 상품 저장/삭제 시 이미지도 함께 저장/삭제
    // orphanRemoval = true 리스트에서 제거하면 DB에서도 삭제
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @Builder
    public Product(Category category, String name, Long price, String description, String thumbnailUrl, ProductStatus status, boolean isDisplayed, int stockQuantity) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
        this.isDisplayed = isDisplayed;
        this.stockQuantity = stockQuantity;
    }

    // --- 비즈니스 로직 (Setter 대신 사용) ---

    // 상태 변경 (Admin API용)
    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    // 정보 수정
    public void updateInfo(String name, Long price, String description, boolean isDisplayed) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.isDisplayed = isDisplayed;
    }

    // 대표 이미지 변경
    public void changeThumbnail(String url) {
        this.thumbnailUrl = url;
    }

    // 이미지 추가 편의 메서드
    public void addImage(ProductImage image) {
        this.images.add(image);
        image.assignProduct(this); // 양방향 연관관계 설정
    }

    // 재고 감소 비즈니스 로직 추가
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다. (현재 재고: " + this.stockQuantity + ")");
        }
        this.stockQuantity = restStock;
    }

    // 재고 증가 로직 (주문 취소 시 필요)
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
