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

    // --- 생성 메서드 ---
    public static OrderItem createOrderItem(Product product, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.product = product;
        orderItem.orderPrice = product.getPrice().intValue(); // Long -> int 변환
        orderItem.count = count;

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
}
