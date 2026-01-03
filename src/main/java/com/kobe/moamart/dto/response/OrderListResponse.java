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
    private int totalPrice; // 총 주문금액 (원래 주문 금액)
    private OrderStatus status; // 주문 상태
    private LocalDateTime orderDate; // 주문 시간
    private LocalDateTime pickupDateTime; // 픽업 예정 일시
    
    // 재무제표 관리용 금액
    private int returnedAmount; // 반품 금액 (상품 상태가 RETURNED인 상품들의 총액)
    private int cancelAmount; // 취소 금액 (상품 상태가 CANCEL인 상품들의 총액)
    private int finalPaymentAmount; // 총 결제 금액 (반품/취소 제외한 실제 결제 금액)

    public OrderListResponse(Order order) {
        this.id = order.getId();
        this.recipientName = order.getRecipientName();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();
        this.pickupDateTime = order.getPickupDateTime();

        // 상품명 요약 로직
        if (order.getOrderItems().isEmpty()) {
            this.productSummary = "상품 정보 없음";
            this.returnedAmount = 0;
            this.cancelAmount = 0;
            this.finalPaymentAmount = 0;
        } else {
            String firstProductName = order.getOrderItems().get(0).getProduct().getName();
            int count = order.getOrderItems().size();

            if (count > 1) {
                this.productSummary = firstProductName + " 외 " + (count - 1) + "건";
            } else {
                this.productSummary = firstProductName;
            }

            // 반품/취소 금액 계산
            this.returnedAmount = order.getOrderItems().stream()
                    .filter(item -> item.getStatus() == OrderStatus.RETURNED)
                    .mapToInt(item -> item.getOrderPrice() * item.getCount())
                    .sum();

            this.cancelAmount = order.getOrderItems().stream()
                    .filter(item -> item.getStatus() == OrderStatus.CANCEL)
                    .mapToInt(item -> item.getOrderPrice() * item.getCount())
                    .sum();

            // 총 결제 금액 = 원래 주문 금액 - 반품 금액 - 취소 금액
            this.finalPaymentAmount = this.totalPrice - this.returnedAmount - this.cancelAmount;
        }
    }
}
