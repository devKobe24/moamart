package com.kobe.moamart.domain.order.repository;

import com.kobe.moamart.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
