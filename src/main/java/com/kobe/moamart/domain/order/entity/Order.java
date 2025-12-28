package com.kobe.moamart.domain.order.entity;

import com.kobe.moamart.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.kobe.moamart.domain.order.entity
 * fileName       : Order
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */

@Entity
@Table(name = "orders") // SQL 예약어 'ORDER'와 겹치지 않게 이름 지정 필수
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    // 비회원 주문이므로 회원 연관관계 대신 직접 정보를 저장
    private String recipientName; // 받는 사람 이름
    private String deliveryAddress; // 배송 주소
    private String phoneNumber; // 연락처

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태

    private LocalDateTime orderDate; // 주문 시간

    // 주문 상품들 (1:N 관계)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // --- 생성 메서드 (비즈니스 로직) ---
    public static Order createOrder(String name, String address, String phone, List<OrderItem> orderItems) {
        Order order = new Order();
        order.recipientName = name;
        order.deliveryAddress = address;
        order.phoneNumber = phone;
        order.status = OrderStatus.ORDER;
        order.orderDate = LocalDateTime.now();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return order;
    }

    // 연관관계 편의 메서드
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }

    // 전체 주문 가격 조회
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getOrderPrice)
                .sum();
    }

    // 상태 변경 비즈니스로직
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
