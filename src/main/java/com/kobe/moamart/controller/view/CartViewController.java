package com.kobe.moamart.controller.view;

import com.kobe.moamart.dto.cart.CartItem;
import com.kobe.moamart.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * packageName    : com.kobe.moamart.controller.view
 * fileName       : CartViewController
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
public class CartViewController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        // 1. 세션에서 장바구니 목록 가져오기
        List<CartItem> cart = cartService.getCartFromSession(session);

        // 2. 각 장바구니 아이템의 재고 수량 최신화 (실시간 재고 확인)
        cartService.refreshCartItemStockQuantities(cart, session);

        // 3. 장바구니 총 금액 계산
        long totalPrice = cart.stream()
                .mapToLong(CartItem::getTotalPrice)
                .sum();

        // 4. 모델에 담기
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);

        return "cart"; // templates/cart.html
    }
}
