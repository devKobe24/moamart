package com.kobe.moamart.controller.admin;

import com.kobe.moamart.domain.order.entity.OrderStatus;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import com.kobe.moamart.dto.response.AdminOrderDetailResponse;
import com.kobe.moamart.dto.response.OrderListResponse;
import com.kobe.moamart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * packageName    : com.kobe.moamart.controller.admin
 * fileName       : AdminOrderController
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final ProductRepository productRepository;

    /**
     * 주문 목록 페이지
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String status,
            Model model
    ) {
        OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = OrderStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값은 무시하고 전체 조회
            }
        }
        
        List<OrderListResponse> orders = orderService.getOrderList(orderStatus);
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", orderStatus);
        return "admin/order/list"; // templates/admin/order/list.html
    }

    /**
     * 주문 상태 변경 API (AJAX 호출용)
     */
    @PatchMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        OrderStatus newStatus = OrderStatus.valueOf(statusStr); // 문자열 -> Enum 변환

        orderService.updateOrderStatus(id, newStatus);

        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        AdminOrderDetailResponse order = orderService.getOrderDetail(id);
        model.addAttribute("order", order);
        // 교환 모달에서 상품 선택을 위한 전체 상품 목록 전달
        model.addAttribute("products", productRepository.findAll());
        return "admin/order/detail"; // templates/admin/order/detail.html
    }

    /**
     * 주문 상품(OrderItem) 상태 변경 API (AJAX 호출용)
     */
    @PatchMapping("/items/{itemId}/status")
    @ResponseBody
    public ResponseEntity<String> updateOrderItemStatus(@PathVariable Long itemId, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        OrderStatus newStatus = OrderStatus.valueOf(statusStr); // 문자열 -> Enum 변환

        orderService.updateOrderItemStatus(itemId, newStatus);

        return ResponseEntity.ok("상품 상태가 변경되었습니다.");
    }

    /**
     * 주문 상품(OrderItem) 수량 변경 API (AJAX 호출용)
     */
    @PatchMapping("/items/{itemId}/count")
    @ResponseBody
    public ResponseEntity<?> updateOrderItemCount(@PathVariable Long itemId, @RequestBody Map<String, Integer> request) {
        try {
            Integer count = request.get("count");
            if (count == null || count < 1) {
                return ResponseEntity.badRequest().body("수량은 최소 1개 이상이어야 합니다.");
            }
            orderService.updateOrderItemCount(itemId, count);
            return ResponseEntity.ok("수량이 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * OrderItem 분리 API (부분 반품/교환용)
     * 같은 상품의 일부만 다른 상태로 처리할 때 사용
     * POST /admin/orders/items/{itemId}/split
     * Body: { "splitCount": 1, "status": "RETURNED" }
     */
    @PostMapping("/items/{itemId}/split")
    @ResponseBody
    public ResponseEntity<?> splitOrderItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Integer splitCount = null;
            Object splitCountObj = request.get("splitCount");
            if (splitCountObj instanceof Integer) {
                splitCount = (Integer) splitCountObj;
            } else if (splitCountObj instanceof Number) {
                splitCount = ((Number) splitCountObj).intValue();
            }

            String statusStr = (String) request.get("status");
            
            if (splitCount == null || splitCount <= 0) {
                return ResponseEntity.badRequest().body("분리할 수량은 1개 이상이어야 합니다.");
            }
            
            if (statusStr == null || statusStr.isEmpty()) {
                return ResponseEntity.badRequest().body("상태를 지정해야 합니다.");
            }

            OrderStatus newStatus = OrderStatus.valueOf(statusStr);
            orderService.splitOrderItem(itemId, splitCount, newStatus);
            
            return ResponseEntity.ok("상품이 분리되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * OrderItem 교환 정보 업데이트 API
     * PATCH /admin/orders/items/{itemId}/exchange
     * Body: { "productName": "새 상품명", "orderPrice": 10000, "count": 2 }
     */
    @PatchMapping("/items/{itemId}/exchange")
    @ResponseBody
    public ResponseEntity<?> updateOrderItemExchange(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            String productName = (String) request.get("productName");
            
            Integer orderPrice = null;
            Object orderPriceObj = request.get("orderPrice");
            if (orderPriceObj instanceof Integer) {
                orderPrice = (Integer) orderPriceObj;
            } else if (orderPriceObj instanceof Number) {
                orderPrice = ((Number) orderPriceObj).intValue();
            }

            Integer count = null;
            Object countObj = request.get("count");
            if (countObj instanceof Integer) {
                count = (Integer) countObj;
            } else if (countObj instanceof Number) {
                count = ((Number) countObj).intValue();
            }

            if (productName == null || productName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("상품명이 필요합니다.");
            }
            if (orderPrice == null) {
                return ResponseEntity.badRequest().body("가격이 필요합니다.");
            }
            if (count == null) {
                return ResponseEntity.badRequest().body("수량이 필요합니다.");
            }

            orderService.updateOrderItemExchange(itemId, productName.trim(), orderPrice, count);
            return ResponseEntity.ok("교환 정보가 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
