package com.kobe.moamart.service;

import com.kobe.moamart.domain.order.entity.Order;
import com.kobe.moamart.domain.order.entity.OrderItem;
import com.kobe.moamart.domain.order.entity.OrderStatus;
import com.kobe.moamart.domain.order.repository.OrderRepository;
import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import com.kobe.moamart.dto.cart.CartItem;
import com.kobe.moamart.dto.response.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 주문 생성
     */
    @Transactional // 쓰기 작업이으모 필수! (중간에 에러나면 전체 롤백됨)
    public Long order(String name, String address, String phone, List<CartItem> cartItems) {

        // 1. 주문 상품 리스트 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem: cartItems) {
            // DB에서 최신 상품 정보를 다시 조회 (가격 변동, 재고 확인 등)
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // 주문 상품 생성 (이때 product.removeStock()이 호출되어 재고가 줄어듦)
            OrderItem orderItem = OrderItem.createOrderItem(product, cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        // 2. 주문 생성
        Order order = Order.createOrder(name, address, phone, orderItems);

        // 3. 주문 저장 (Cascade 옵션 때문에 orderItems도 같이 저장됨)
        orderRepository.save(order);

        return order.getId();
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
}
