package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * packageName    : com.kobe.moamart.dto.response
 * fileName       : OrderListResponse
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Getter
public class OrderListResponse {
    private Long id;
    private String recipientName; // 주문자 (수령인)
    private String productSummary; // 상품명 요약 (예: 고래밥 외 2건)
    private int totalPrice; // 총 주문금액
    private OrderStatus status; // 주문 상태
    private LocalDateTime orderDate; // 주문 시간

    public OrderListResponse(Order order) {
        this.id = order.getId();
        this.recipientName = order.getRecipientName();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();

        // 상품명 요약 로직
        if (order.getOrderItems().isEmpty()) {
            this.productSummary = "상품 정보 없음";
        } else {
            String firstProductName = order.getOrderItems().get(0).getProduct().getName();
            int count = order.getOrderItems().size();

            if (count > 1) {
                this.productSummary = firstProductName + " 외 " + (count - 1) + "건";
            } else {
                this.productSummary = firstProductName;
            }
        }
    }
}
