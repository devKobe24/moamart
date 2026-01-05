package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 고객용 주문 상세 정보 응답 DTO
 */
@Getter
public class OrderDetailResponse {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String deliveryAddress;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime pickupDateTime;
    private String storeName;
    private List<OrderItemDto> orderItems;
    private int totalPrice;

    @Getter
    public static class OrderItemDto {
        private Long id;
        private String productName;
        private int orderPrice;
        private int count;
        private OrderStatus status;

        public OrderItemDto(com.kobe.moamart.domain.order.entity.OrderItem orderItem) {
            this.id = orderItem.getId();
            this.productName = orderItem.getProduct().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
            this.status = orderItem.getStatus();
        }
    }

    public OrderDetailResponse(Order order) {
        this.id = order.getId();
        this.recipientName = order.getRecipientName();
        this.phoneNumber = order.getPhoneNumber();
        this.deliveryAddress = order.getDeliveryAddress();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();
        this.pickupDateTime = order.getPickupDateTime();
        this.storeName = order.getStore() != null ? order.getStore().getName() : null;
        this.totalPrice = order.getTotalPrice();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }
}

