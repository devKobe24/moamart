package com.kobe.moamart.service;

import com.kobe.moamart.domain.order.entity.BagType;
import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderItem;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import com.kobe.moamart.domain.order.repository.OrderRepository;
import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import com.kobe.moamart.domain.store.entity.Store;
import com.kobe.moamart.domain.store.repository.StoreRepository;
import com.kobe.moamart.dto.cart.CartItem;
import com.kobe.moamart.dto.response.AdminOrderDetailResponse;
import com.kobe.moamart.dto.response.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.kobe.moamart.service
 * fileName       : OrderService
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    /**
     * 픽업 주문 생성
     */
    @Transactional
    public Long createPickupOrder(String name, String phone, Long storeId, BagType bagType, LocalDateTime pickupDateTime, List<CartItem> cartItems) {
        // 1. 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));

        // 2. 주문 상품 리스트 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem: cartItems) {
            // DB에서 최신 상품 정보를 다시 조회 (가격 변동, 재고 확인 등)
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // 주문 상품 생성 (이때 product.removeStock()이 호출되어 재고가 줄어듦)
            OrderItem orderItem = OrderItem.createOrderItem(product, cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        // 3. 픽업 주문 생성
        Order order = Order.createPickupOrder(name, phone, store, bagType, pickupDateTime, orderItems);

        // 4. 주문 저장 (Cascade 옵션 때문에 orderItems도 같이 저장됨)
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 생성 (기존 메서드 - 호환성 유지)
     * @deprecated 픽업 서비스만 사용하므로 사용하지 않음
     */
    @Deprecated
    @Transactional
    public Long order(String name, String address, String phone, List<CartItem> cartItems) {
        // 픽업 서비스만 사용하므로 이 메서드는 사용하지 않음
        throw new UnsupportedOperationException("일반 배송은 지원하지 않습니다. 픽업 서비스를 이용해주세요.");
    }

    /**
     * 봉투 가격 계산
     */
    public long calculateBagPrice(BagType bagType) {
        if (bagType == null || bagType == BagType.NONE) {
            return 0;
        }
        return switch (bagType) {
            case BAG_10L -> 330;
            case BAG_20L -> 660;
            default -> 0;
        };
    }

    /**
     * 관리자용: 전체 주문 조회 (최신순)
     */
    public List<OrderListResponse> getOrderList() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(OrderListResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 관리자용: 주문 상태 변경
     */
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.changeStatus(status);
    }

    /**
     * 관리자용: 주문 상세 조회
     */
    public AdminOrderDetailResponse getOrderDetail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return new AdminOrderDetailResponse(order);
    }
}
