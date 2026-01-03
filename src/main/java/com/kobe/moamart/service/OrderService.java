package com.kobe.moamart.service;

import com.kobe.moamart.domain.order.entity.BagType;
import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderItem;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import com.kobe.moamart.domain.order.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;
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
     * @param status 주문 상태 (null이면 전체 조회)
     */
    public List<OrderListResponse> getOrderList(OrderStatus status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Order> orders;
        
        if (status != null) {
            orders = orderRepository.findByStatus(status, sort);
        } else {
            orders = orderRepository.findAll(sort);
        }
        
        return orders.stream()
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

    /**
     * 관리자용: 주문 상품(OrderItem) 상태 변경
     */
    @Transactional
    public void updateOrderItemStatus(Long orderItemId, OrderStatus status) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("주문 상품을 찾을 수 없습니다."));
        orderItem.changeStatus(status);
    }

    /**
     * 관리자용: 주문 상품(OrderItem) 수량 변경
     */
    @Transactional
    public void updateOrderItemCount(Long orderItemId, int newCount) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("주문 상품을 찾을 수 없습니다."));
        orderItem.changeCount(newCount);
    }

    /**
     * 관리자용: OrderItem을 분리하고 상태를 변경
     * 같은 상품의 일부만 다른 상태(반품, 교환 등)로 처리하기 위한 메서드
     * 
     * @param orderItemId 분리할 OrderItem ID
     * @param splitCount 분리할 수량
     * @param newStatus 분리된 Item의 상태 (RETURNED, EXCHANGE 등)
     */
    @Transactional
    public void splitOrderItem(Long orderItemId, int splitCount, OrderStatus newStatus) {
        OrderItem originalItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("주문 상품을 찾을 수 없습니다."));

        // 분리 가능 여부 확인
        if (splitCount >= originalItem.getCount() || splitCount <= 0) {
            throw new IllegalArgumentException("분리할 수량이 올바르지 않습니다. (현재 수량: " + originalItem.getCount() + ")");
        }

        // OrderItem 분리
        OrderItem splitItem = originalItem.split(splitCount);

        // 분리된 Item의 상태 변경
        splitItem.changeStatus(newStatus);

        // 분리된 Item 저장
        orderItemRepository.save(splitItem);

        // 원본 Item도 저장 (수량이 변경되었으므로)
        orderItemRepository.save(originalItem);
    }
}
