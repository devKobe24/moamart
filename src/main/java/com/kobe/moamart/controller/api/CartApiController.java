package com.kobe.moamart.controller.api;

import com.kobe.moamart.dto.request.CartAddRequest;
import com.kobe.moamart.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.kobe.moamart.controller.api
 * fileName       : CartApiController
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addCart(@Valid @RequestBody CartAddRequest request, HttpSession session) {
        cartService.addCart(request.getProductId(), request.getQuantity(), session);
        return ResponseEntity.ok("장바구니에 담겼습니다.");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long productId, HttpSession session) {
        cartService.removeCartItem(productId, session);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}
