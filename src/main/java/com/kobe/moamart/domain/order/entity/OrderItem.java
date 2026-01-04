package com.kobe.moamart.domain.order.entity;

import com.kobe.moamart.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.kobe.moamart.domain.order.entity
 * fileName       : OrderItem
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 당시 가격
    private int count; // 주문 수량

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 상품 상태 (각 상품별 상태 관리)

    // --- 생성 메서드 ---
    public static OrderItem createOrderItem(Product product, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.product = product;
        orderItem.orderPrice = product.getPrice().intValue(); // Long -> int 변환
        orderItem.count = count;
        orderItem.status = OrderStatus.ORDER; // 기본 상태: 주문완료

        // 상품 재고 감소
        // 재고가 부족하면 여기서 예외가 발생하여 주문이 진행되지 않습니다.
        product.removeStock(count);

        return orderItem;
    }

    // 연관관계 편의 메서드

    public void assignOrder(Order order) {
        this.order = order;
    }

    // 해당 아이템 총액 (가격 * 수량)
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

    // 상품 상태 변경
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    // 수량 변경
    public void changeCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("수량은 최소 1개 이상이어야 합니다.");
        }
        this.count = count;
    }

    // 상품 변경 (교환 시 사용)
    public void changeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("상품은 null일 수 없습니다.");
        }
        this.product = product;
    }

    // 가격 변경 (교환 시 사용)
    public void changeOrderPrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        this.orderPrice = price;
    }

    /**
     * OrderItem을 분리하여 새로운 OrderItem을 생성
     * 같은 상품의 일부만 다른 상태로 처리하기 위한 메서드
     * 재고 처리는 하지 않음 (이미 주문 시 차감됨)
     * 
     * @param splitCount 분리할 수량
     * @return 분리된 새로운 OrderItem
     */
    public OrderItem split(int splitCount) {
        if (splitCount >= this.count || splitCount <= 0) {
            throw new IllegalArgumentException("분리할 수량은 현재 수량보다 작고 0보다 커야 합니다.");
        }

        // 새로운 OrderItem 생성 (재고 처리 없이)
        OrderItem splitItem = new OrderItem();
        splitItem.product = this.product;
        splitItem.orderPrice = this.orderPrice;
        splitItem.count = splitCount;
        splitItem.status = OrderStatus.ORDER; // 기본 상태, 나중에 상태 변경 가능
        splitItem.order = this.order;

        // 원본 OrderItem의 수량 감소
        this.count -= splitCount;

        return splitItem;
    }
}
