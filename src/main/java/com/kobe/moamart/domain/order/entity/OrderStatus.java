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
    PREPARING, // 상품 준비중
    READY_FOR_PICKUP, // 상품 준비 완료
    PICKUP_COMPLETED, // 픽업 완료
    RETURNED ,// 반품 완료
    EXCHANGE, // 상품 교환
    OUT_OF_STOCK, // 품절
    CANCEL // 주문 취소
}
