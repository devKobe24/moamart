package com.kobe.moamart.service;

import com.kobe.moamart.domain.category.Category;
import com.kobe.moamart.domain.category.CategoryRepository;
import com.kobe.moamart.domain.order.repository.OrderItemRepository;
import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductImage;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import com.kobe.moamart.dto.request.ProductSaveRequest;
import com.kobe.moamart.dto.request.ProductSearchCondition;
import com.kobe.moamart.dto.response.ProductDetailResponse;
import com.kobe.moamart.dto.response.ProductListResponse;
import com.kobe.moamart.global.util.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * packageName    : com.kobe.moamart.service
 * fileName       : ProductService
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 성능 최적화
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // 카테고리 조회용 추가
    private final FileUploader fileUploader; // 파일 업로더 추가
    private final OrderItemRepository orderItemRepository; // 주문 수량 조회용

    /**
     * 관리자용 상품 목록 조회 (페이징 + 검색)
     */
    public Page<ProductListResponse> getAdminProductList(ProductSearchCondition condition, Pageable pageable) {
        // 1. Repository에서 Entity Page 조회 (QueryDSL)
        return productRepository.search(condition, pageable)
                // 2. Entity -> DTO로 변환 (주문 수량 포함)
                .map(product -> {
                    int totalOrderedQuantity = orderItemRepository.getTotalOrderedQuantity(product.getId());
                    return new ProductListResponse(product, totalOrderedQuantity);
                });
    }

    /**
     * 상품 상태 변경 (Dirty Checking 감지)
     */
    @Transactional
    public void updateStatus(Long productId, ProductStatus newStatus) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + productId));

        product.changeStatus(newStatus);
        // @Transactional 덕분에 save()를 호출하지 않아도 자동으로 update 쿼리가 나갑니다.
    }

    /**
     * 상품 등록 (이미지 업로드 포함)
     */
    @Transactional // 쓰기 작업이므로 필수
    public Long saveProduct(ProductSaveRequest request) {
        // 1. 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 2. 대표 이미지 업로드 처리 (썸네일로 작은 크기로 리사이징)
        String thumbnailUrl = null;
        if (request.getThumbnailImage() != null && !request.getThumbnailImage().isEmpty()) {
            thumbnailUrl = fileUploader.upload(request.getThumbnailImage(), true); // true = 썸네일
        }

        // 3. 상품 Entity 생성 및 저장
        Product product = Product.builder()
                .category(category)
                .name(request.getName())
                .price(request.getPrice())
                .status(request.getStatus())
                .description(request.getDescription())
                .thumbnailUrl(thumbnailUrl)
                .isDisplayed(true)
                .isNew(request.getIsNew() != null ? request.getIsNew() : false)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .build();

        // 4. 상세 이미지들 업로드 및 연관관계 설정
        if (request.getProductImages() != null) {
            for (MultipartFile file : request.getProductImages()) {
                if (!file.isEmpty()) {
                    String uploadUrl = fileUploader.upload(file);

                    // ProductImage 엔티티 생성
                    ProductImage image = ProductImage.builder()
                            .url(uploadUrl)
                            .isThumbnail(false)
                            .displayOrder(0) // 순서는 나중에 구현
                            .build();

                    // 연관관계 편의 메서드 사용 (Product 내부에 addImage 구현 필요)
                    product.addImage(image);
                }
            }
        }

        // 5. 최종 저장 (Cascade 설정 덕분에 productImages도 같이 저장됨)
        return productRepository.save(product).getId();
    }

    /**
     * 메인 페이지 최신 상품 조회 (isNew가 true이고 상태가 STOP이 아닌 상품만)
     */
    public Page<ProductListResponse> getNewProducts(Long categoryId, Pageable pageable) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndIsDisplayedTrueAndIsNewTrueAndStatusNotOrderByIdDesc(categoryId, ProductStatus.STOP, pageable)
                    .map(ProductListResponse::new);
        } else {
            return productRepository.findAllByIsDisplayedTrueAndIsNewTrueAndStatusNotOrderByIdDesc(ProductStatus.STOP, pageable)
                    .map(ProductListResponse::new);
        }
    }

    /**
     * 메인 페이지 전체 상품 조회 (isDisplayed가 true이고 상태가 STOP이 아닌 모든 상품)
     */
    public Page<ProductListResponse> getAllProducts(Long categoryId, Pageable pageable) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndIsDisplayedTrueAndStatusNotOrderByIdDesc(categoryId, ProductStatus.STOP, pageable)
                    .map(ProductListResponse::new);
        } else {
        return productRepository.findAllByIsDisplayedTrueAndStatusNotOrderByIdDesc(ProductStatus.STOP, pageable)
                .map(ProductListResponse::new);
        }
    }

    /**
     * 상품 상세 조회
     */
    public ProductDetailResponse getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));

        return new ProductDetailResponse(product);
    }

    /**
     * 상품 수정을 위한 조회 (ProductSaveRequest 반환)
     */
    public ProductSaveRequest getProductForEdit(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));

        ProductSaveRequest request = new ProductSaveRequest();
        request.setCategoryId(product.getCategory().getId());
        request.setName(product.getName());
        request.setPrice(product.getPrice());
        request.setStatus(product.getStatus());
        request.setDescription(product.getDescription());
        request.setStockQuantity(product.getStockQuantity());
        request.setIsNew(product.isNew());

        return request;
    }

    /**
     * 상품 수정 (이미지 업로드 포함)
     */
    @Transactional
    public void updateProduct(Long id, ProductSaveRequest request) {
        // 1. 기존 상품 조회
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));

        // 2. 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 3. 기본 정보 수정
        product.updateInfo(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                product.isDisplayed() // isDisplayed는 수정 폼에 없으므로 기존 값 유지
        );

        // 4. 카테고리, 상태, 재고, 최신 상품 여부 수정
        product.changeCategory(category);
        if (request.getStatus() != null) {
            product.changeStatus(request.getStatus());
        }
        if (request.getStockQuantity() != null) {
            product.changeStockQuantity(request.getStockQuantity());
        }
        if (request.getIsNew() != null) {
            product.changeIsNew(request.getIsNew());
        }

        // 5. 대표 이미지 업로드 처리 (새 이미지가 있는 경우만)
        if (request.getThumbnailImage() != null && !request.getThumbnailImage().isEmpty()) {
            // 기존 썸네일 이미지 삭제
            if (product.getThumbnailUrl() != null) {
                fileUploader.delete(product.getThumbnailUrl());
            }
            
            String thumbnailUrl = fileUploader.upload(request.getThumbnailImage(), true); // true = 썸네일
            product.changeThumbnail(thumbnailUrl);
        }

        // 6. 새로운 상세 이미지 추가 (있는 경우만)
        if (request.getProductImages() != null) {
            for (MultipartFile file : request.getProductImages()) {
                if (!file.isEmpty()) {
                    String uploadUrl = fileUploader.upload(file);

                    ProductImage image = ProductImage.builder()
                            .url(uploadUrl)
                            .isThumbnail(false)
                            .displayOrder(0)
                            .build();

                    product.addImage(image);
                }
            }
        }

        // @Transactional에 의해 자동으로 업데이트됨 (명시적으로 save 호출 불필요)
    }

    /**
     * 상품 삭제 (이미지 파일도 함께 삭제)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));

        // 1. 썸네일 이미지 삭제
        if (product.getThumbnailUrl() != null) {
            fileUploader.delete(product.getThumbnailUrl());
        }

        // 2. 상세 이미지들 삭제
        for (ProductImage image : product.getImages()) {
            if (image.getUrl() != null) {
                fileUploader.delete(image.getUrl());
            }
        }

        // 3. DB에서 상품 삭제 (Cascade 설정으로 이미지도 함께 삭제됨)
        productRepository.delete(product);
    }
}
