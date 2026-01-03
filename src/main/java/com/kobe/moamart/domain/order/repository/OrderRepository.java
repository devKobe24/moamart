package com.kobe.moamart.domain.order.repository;

import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName    : com.kobe.moamart.domain.order.repository
 * fileName       : OrderRepository
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 주문 상태로 주문 목록 조회
     */
    List<Order> findByStatus(OrderStatus status, Sort sort);
}
