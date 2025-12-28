package com.kobe.moamart.dto.request;

import com.kobe.moamart.domain.product.entity.ProductStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.kobe.moamart.dto.request
 * fileName       : ProductStatusRequest
 * author         : kobe
 * date           : 2025. 12. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 27.        kobe       최초 생성
 */

@Data
@NoArgsConstructor
public class ProductStatusRequest {
    private ProductStatus status; // 변경할 상태 (SELL, SOLD_OUT, STOP)
}
