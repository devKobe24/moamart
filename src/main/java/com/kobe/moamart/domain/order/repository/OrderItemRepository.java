package com.kobe.moamart.domain.order.repository;

import com.kobe.moamart.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * packageName    : com.kobe.moamart.domain.order.repository
 * fileName       : OrderItemRepository
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : OrderItem Repository
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 특정 상품의 주문된 총 수량 계산
     * @param productId 상품 ID
     * @return 주문된 총 수량 (없으면 0)
     */
    @Query("SELECT COALESCE(SUM(oi.count), 0) FROM OrderItem oi WHERE oi.product.id = :productId")
    int getTotalOrderedQuantity(@Param("productId") Long productId);
}

