package com.kobe.moamart.controller.view;

import com.kobe.moamart.domain.order.entity.BagType;
import com.kobe.moamart.dto.cart.CartItem;
import com.kobe.moamart.service.CartService;
import com.kobe.moamart.service.OrderService;
import com.kobe.moamart.service.StoreService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final StoreService storeService;

    /**
     * 주문 검토 페이지 (픽업 서비스)
     */
    @GetMapping("/orders/review")
    public String reviewOrder(HttpSession session, Model model) {
        // 장바구니가 비어있으면 장바구니 페이지로 튕겨냄
        List<CartItem> cart = cartService.getCartFromSession(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // 각 장바구니 아이템의 재고 수량 최신화 (실시간 재고 확인)
        cartService.refreshCartItemStockQuantities(cart, session);

        // 활성화된 매장 목록 조회
        var stores = storeService.getActiveStores();
        if (stores.isEmpty()) {
            model.addAttribute("errorMessage", "픽업 가능한 매장이 없습니다. 관리자에게 문의해주세요.");
            return "cart";
        }

        // 상품 금액 계산
        long totalPrice = cart.stream()
                .mapToLong(CartItem::getTotalPrice)
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("stores", stores);
        model.addAttribute("totalPrice", totalPrice);

        return "orders/review"; // templates/orders/review.html
    }

    /**
     * 주문서 작성 페이지 (GET) - 픽업 서비스
     */
    @GetMapping("/orders/checkout")
    public String checkoutForm(
            @RequestParam Long storeId,
            @RequestParam(required = false) String bagType,
            HttpSession session,
            Model model
    ) {
        // 장바구니가 비어있으면 장바구니 페이지로 튕겨냄
        List<CartItem> cart = cartService.getCartFromSession(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // 매장 조회
        var store = storeService.getStore(storeId);

        // 상품 금액 계산
        long totalPrice = cart.stream()
                .mapToLong(CartItem::getTotalPrice)
                .sum();

        // 봉투 가격 계산
        BagType bagTypeEnum = bagType != null && !bagType.isEmpty() 
                ? BagType.valueOf(bagType) 
                : BagType.NONE;
        long bagPrice = orderService.calculateBagPrice(bagTypeEnum);

        // 총 결제 금액
        long finalPrice = totalPrice + bagPrice;

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("bagPrice", bagPrice);
        model.addAttribute("finalPrice", finalPrice);
        model.addAttribute("store", store);
        model.addAttribute("storeId", storeId);
        model.addAttribute("bagType", bagTypeEnum);

        return "order/checkout"; // templates/order/checkout.html
    }

    /**
     * 주문 처리 (POST) - 픽업 주문
     */
    @PostMapping("/orders/checkout")
    public String processOrder(
            @RequestParam String recipientName,
            @RequestParam String phoneNumber,
            @RequestParam Long storeId,
            @RequestParam(required = false) String bagType,
            @RequestParam(defaultValue = "00") String pickupHour,
            @RequestParam(defaultValue = "00") String pickupMinute,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        List<CartItem> cart = cartService.getCartFromSession(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            // 봉투 타입 파싱
            BagType bagTypeEnum = bagType != null && !bagType.isEmpty() 
                    ? BagType.valueOf(bagType) 
                    : BagType.NONE;

            // 픽업 시간 파싱 (시간과 분을 받아서 LocalDateTime 생성)
            int hour = Integer.parseInt(pickupHour);
            int minute = Integer.parseInt(pickupMinute);
            LocalDateTime pickupDateTime = LocalDate.now()
                    .atTime(hour, minute);

            // 픽업 주문 생성
            orderService.createPickupOrder(recipientName, phoneNumber, storeId, bagTypeEnum, pickupDateTime, cart);

            // 주문 성공 시 장바구니 비우기
            cartService.clearCart(session);

            // 완료 페이지로 리다이렉트
            return "redirect:/orders/complete";
        } catch (Exception e) {
            // 재고 부족 등의 에러 발생 시 다시 주문 검토 페이지로
            redirectAttributes.addFlashAttribute("errorMessage", "주문 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/orders/review";
        }
    }

    /**
     * 주문 완료 페이지 (GET)
     */
    @GetMapping("/orders/complete")
    public String orderComplete() {
        return "order/complete"; // templates/order/complete.html
    }

    /**
     * 주문 내역 페이지 (사용자용)
     */
    @GetMapping("/orders")
    public String orderList(Model model) {
        // 모든 주문 목록 조회 (사용자별 필터링은 추후 인증 기능과 연동 시 추가)
        var orders = orderService.getOrderList(null);
        model.addAttribute("orders", orders);
        return "orders/list"; // templates/orders/list.html
    }

    /**
     * 고객용 주문 상세 페이지
     */
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        var orderDetail = orderService.getOrderDetailForCustomer(id);
        model.addAttribute("order", orderDetail);
        return "orders/detail"; // templates/orders/detail.html
    }
}
