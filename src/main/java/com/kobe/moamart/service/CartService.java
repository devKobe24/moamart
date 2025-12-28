package com.kobe.moamart.service;

import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.repository.ProductRepository;
import com.kobe.moamart.dto.cart.CartItem;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.kobe.moamart.service
 * fileName       : CartService
 * author         : kobe
 * date           : 2025. 12. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 28.        kobe       최초 생성
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductService productService;
    private static final String CART_SESSION_KEY = "MY_CART";
    private final ProductRepository productRepository;

    /**
     * 장바구니 담기
     */
    public void addCart(Long productId, int quantity, HttpSession session) {
        // 1. 세션에서 장바구니 목록 가져오기 (없으면 생성)
        List<CartItem> cart = getCartFromSession(session);

        // 2. 이미 장바구니에 있는 상품인지 확인
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // 이미 있으면 수량만 증가
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // 없으면 새로 추가
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setThumbnailUrl(product.getThumbnailUrl());
            newItem.setQuantity(quantity);

            cart.add(newItem);
        }

        // 3. 세션에 다시 저장
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * 세션에서 장바구니 목록 조회
     */
    @SuppressWarnings("unchecked")
    public List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    /**
     * 장바구니 아이템 삭제
     */
    public void removeCartItem(Long productId, HttpSession session) {
        List<CartItem> cart = getCartFromSession(session);
        // ID가 같은 상품을 리스트에서 제거
        cart.removeIf(item -> item.getProductId().equals(productId));
        // 변경된 리스트를 세션에 다시 저장
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * 장바구니 비우기 (주문 완료 후 사용)
     */
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
