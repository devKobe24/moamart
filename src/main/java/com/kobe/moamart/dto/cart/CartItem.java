package com.kobe.moamart.dto.cart;

import lombok.Data;

/**
 * packageName    : com.kobe.moamart.dto.cart
 * fileName       : CartItem
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
@Data
public class CartItem {
    private Long productId;
    private String name;
    private Long price;
    private String thumbnailUrl;
    private int quantity; // 수량
    private int stockQuantity; // 재고 수량 (수량 변경 시 최대값 체크용)

    // 수량에 따른 총 가격 계산
    public Long getTotalPrice() {
        return price * quantity;
    }
}
