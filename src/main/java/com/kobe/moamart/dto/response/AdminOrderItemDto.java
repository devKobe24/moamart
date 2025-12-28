package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.order.entity.OrderItem;
import lombok.Getter;

/**
 * packageName    : com.kobe.moamart.dto.response
 * fileName       : AdminOrderItemDto
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Getter
public class AdminOrderItemDto {
    private String productName;
    private int count;
    private int orderPrice; // 주문 당시 가격
    private int totalPrice; // 가격 * 수량

    public AdminOrderItemDto(OrderItem orderItem) {
        this.productName = orderItem.getProduct().getName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }
}
