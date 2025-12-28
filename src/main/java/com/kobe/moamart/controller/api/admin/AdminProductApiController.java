package com.kobe.moamart.controller.api.admin;

import com.kobe.moamart.dto.request.ProductStatusRequest;
import com.kobe.moamart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.kobe.moamart.controller.api.admin
 * fileName       : AdminProductApiController
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */
@RestController // @Controller + @ResponseBody (JSON 변환)
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductApiController {

    private final ProductService productService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody ProductStatusRequest request
    ) {
        productService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok().build(); // 200 OK 반환 (Body 없음)
    }
}
