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
import com.kobe.moamart.dto.response.OrderDetailResponse;
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
     * 주문 상태가 ORDER, PREPARING, READY_FOR_PICKUP, PICKUP_COMPLETED일 때는
     * 주문 상세 정보의 상품 상태도 함께 변경됩니다.
     * 그 외 상태(RETURNED, EXCHANGE, OUT_OF_STOCK, CANCEL)는 주문 상태만 변경됩니다.
     */
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.changeStatus(status);
        
        // 특정 상태일 때는 OrderItem의 상태도 함께 변경
        if (status == OrderStatus.ORDER || 
            status == OrderStatus.PREPARING || 
            status == OrderStatus.READY_FOR_PICKUP || 
            status == OrderStatus.PICKUP_COMPLETED) {
            // 주문의 모든 OrderItem 상태를 주문 상태와 동일하게 변경
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.changeStatus(status);
            }
        }
        // 그 외 상태(RETURNED, EXCHANGE, OUT_OF_STOCK, CANCEL)는 주문 상태만 변경
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
     * 고객용: 주문 상세 정보 조회
     */
    public OrderDetailResponse getOrderDetailForCustomer(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return new OrderDetailResponse(order);
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

    /**
     * 관리자용: 주문 상품(OrderItem) 교환 정보 업데이트
     * 상품명, 단가, 수량을 변경할 수 있음
     */
    @Transactional
    public void updateOrderItemExchange(Long orderItemId, String productName, int orderPrice, int count) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("주문 상품을 찾을 수 없습니다."));

        if (orderItem.getStatus() != OrderStatus.EXCHANGE) {
            throw new IllegalArgumentException("상품 상태가 교환(EXCHANGE)이 아닙니다.");
        }

        // 상품명으로 상품 찾기
        Product newProduct = productRepository.findByName(productName)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productName));

        if (count < 1) {
            throw new IllegalArgumentException("수량은 최소 1개 이상이어야 합니다.");
        }

        if (orderPrice < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        // 기존 상품과 새 상품
        Product oldProduct = orderItem.getProduct();
        int oldCount = orderItem.getCount();

        // 재고 관리: 기존 상품의 재고 반환, 새 상품의 재고 차감
        if (!oldProduct.getId().equals(newProduct.getId())) {
            // 상품이 변경된 경우: 기존 상품 재고 반환, 새 상품 재고 차감
            oldProduct.addStock(oldCount);
            newProduct.removeStock(count);
        } else {
            // 같은 상품인 경우: 수량 차이만 반영
            int countDifference = count - oldCount;
            if (countDifference > 0) {
                // 수량이 증가한 경우: 재고 차감
                newProduct.removeStock(countDifference);
            } else if (countDifference < 0) {
                // 수량이 감소한 경우: 재고 반환
                newProduct.addStock(Math.abs(countDifference));
            }
            // countDifference == 0인 경우 재고 변경 없음
        }

        // 상품, 가격, 수량 변경
        orderItem.changeProduct(newProduct);
        orderItem.changeOrderPrice(orderPrice);
        orderItem.changeCount(count);
    }
}
