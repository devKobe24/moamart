package com.kobe.moamart.controller.admin;

import com.kobe.moamart.domain.order.entity.OrderStatus;
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

    /**
     * 주문 목록 페이지
     */
    @GetMapping
    public String list(Model model) {
        List<OrderListResponse> orders = orderService.getOrderList();
        model.addAttribute("orders", orders);
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
}
