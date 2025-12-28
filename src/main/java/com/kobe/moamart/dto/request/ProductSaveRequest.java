package com.kobe.moamart.dto.request;

import com.kobe.moamart.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.kobe.moamart.dto.request
 * fileName       : ProductSaveRequest
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */
@Data
public class ProductSaveRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    private Long price;

    private ProductStatus status; // 없으면 기본값(SELL) 사용

    private String description;

    // 입고량 (재고 수량)
    private Integer stockQuantity;

    // 최신 상품 여부
    private Boolean isNew;

    // --- 파일 업로드 필드 ---

    // 대표 이미지 (필수 아님 - 없으면 기본 이미지)
    private MultipartFile thumbnailImage;

    // 상세 이미지들 (여러 개 가능)
    private List<MultipartFile> productImages = new ArrayList<>();
}
