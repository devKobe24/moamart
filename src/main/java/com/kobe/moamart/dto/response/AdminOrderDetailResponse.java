package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.kobe.moamart.dto.response
 * fileName       : AdminOrderDetailResponse
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Getter
public class AdminOrderDetailResponse {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;

    // 배송 정보
    private String recipientName;
    private String phoneNumber;
    private String deliveryAddress;

    // 결제 정보
    private int totalPrice;

    // 주문 상품 리스트
    private List<AdminOrderItemDto> orderItems;

    public AdminOrderDetailResponse(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.recipientName = order.getRecipientName();
        this.phoneNumber = order.getPhoneNumber();
        this.deliveryAddress = order.getDeliveryAddress();
        this.totalPrice = order.getTotalPrice();

        this.orderItems = order.getOrderItems().stream()
                .map(AdminOrderItemDto::new)
                .collect(Collectors.toList());
    }
}
