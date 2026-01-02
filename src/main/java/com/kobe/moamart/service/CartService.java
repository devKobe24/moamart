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
        // 1. 상품 조회 (재고 확인용)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 2. 세션에서 장바구니 목록 가져오기 (없으면 생성)
        List<CartItem> cart = getCartFromSession(session);

        // 3. 이미 장바구니에 있는 상품인지 확인
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        int finalQuantity;
        if (existingItem.isPresent()) {
            // 이미 있으면 수량만 증가
            CartItem item = existingItem.get();
            finalQuantity = item.getQuantity() + quantity;
        } else {
            // 없으면 새로 추가
            finalQuantity = quantity;
        }

        // 4. 최종 수량이 입고량을 초과하는지 확인
        if (finalQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    String.format("입고량을 초과할 수 없습니다. (현재 재고: %d개, 요청 수량: %d개)",
                            product.getStockQuantity(), finalQuantity)
            );
        }

        // 5. 수량 업데이트 또는 새 아이템 추가
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(finalQuantity);
            // 기존 아이템의 재고 수량도 업데이트
            item.setStockQuantity(product.getStockQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setThumbnailUrl(product.getThumbnailUrl());
            newItem.setQuantity(quantity);
            newItem.setStockQuantity(product.getStockQuantity());

            cart.add(newItem);
        }

        // 6. 세션에 다시 저장
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
     * 장바구니 아이템 수량 업데이트
     */
    public void updateCartItemQuantity(Long productId, int newQuantity, HttpSession session) {
        // 1. 상품 조회 (재고 확인용)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 2. 세션에서 장바구니 목록 가져오기
        List<CartItem> cart = getCartFromSession(session);

        // 3. 해당 상품 찾기
        CartItem cartItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));

        // 4. 수량 유효성 검사
        if (newQuantity < 1) {
            throw new IllegalArgumentException("수량은 최소 1개 이상이어야 합니다.");
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    String.format("재고량을 초과할 수 없습니다. (현재 재고: %d개, 요청 수량: %d개)",
                            product.getStockQuantity(), newQuantity)
            );
        }

        // 5. 수량 및 재고 수량 업데이트
        cartItem.setQuantity(newQuantity);
        cartItem.setStockQuantity(product.getStockQuantity());

        // 6. 세션에 다시 저장
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * 장바구니 아이템들의 재고 수량 최신화 (페이지 로드 시 호출)
     */
    public void refreshCartItemStockQuantities(List<CartItem> cart, HttpSession session) {
        for (CartItem item : cart) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                item.setStockQuantity(product.getStockQuantity());
                // 현재 수량이 재고를 초과하면 재고량으로 조정
                if (item.getQuantity() > product.getStockQuantity()) {
                    item.setQuantity(product.getStockQuantity());
                }
            }
        }
        // 업데이트된 장바구니를 세션에 다시 저장
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * 장바구니 비우기 (주문 완료 후 사용)
     */
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
