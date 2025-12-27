package com.kobe.moamart.controller.view;

import com.kobe.moamart.dto.cart.CartItem;
import com.kobe.moamart.service.CartService;
import com.kobe.moamart.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * packageName    : com.kobe.moamart.controller.view
 * fileName       : OrderController
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    /**
     * 1. 주문서 작성 페이지 (GET)
     */
    @GetMapping("/orders/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        // 장바구니가 비어있으면 장바구니 페이지로 튕겨냄
        List<CartItem> cart = cartService.getCartFromSession(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // 결제 예상 금액 계산
        long totalPrice = cart.stream()
                .mapToLong(CartItem::getTotalPrice)
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);

        return "order/checkout"; // templates/order/checkout.html
    }

    /**
     * 2. 주문 처리 (POST)
     */
    @PostMapping("/orders/checkout")
    public String processOrder(
            @RequestParam String recipientName,
            @RequestParam String deliveryAddress,
            @RequestParam String phoneNumber,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        List<CartItem> cart = cartService.getCartFromSession(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            // 주문 서비스 호출 (재고 감소, 주문 저장)
            Long orderId = orderService.order(recipientName, deliveryAddress, phoneNumber, cart);

            // 주문 성공 시 장바구니 비우기
            cartService.clearCart(session);

            // 완료 페이지로 리다이렉트
            return "redirect:/orders/complete";
        } catch (Exception e) {
            // 재고 부족 등의 에러 발생 시 다시 주문 페이지로 (에러 메시지 전달)
            redirectAttributes.addFlashAttribute("errorMessage", "주문 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    /**
     * 3. 주문 완료 페이지 (GET)
     */
    @GetMapping("/orders/complete")
    public String orderComplete() {
        return "order/complete"; // templates/order/complete.html
    }
}
