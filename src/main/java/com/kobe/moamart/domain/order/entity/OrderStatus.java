package com.kobe.moamart.domain.order.entity;

/**
 * packageName    : com.kobe.moamart.domain.order.entity
 * fileName       : OrderStatus
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
public enum OrderStatus {
    ORDER, // 주문 완료
    CANCEL, // 주문 취소
    DELIVERY, // 배송 중
    COMPLETED // 배송 완료
}
