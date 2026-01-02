package com.kobe.moamart.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * packageName    : com.kobe.moamart.dto.request
 * fileName       : CartUpdateRequest
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 장바구니 수량 업데이트 요청 DTO
 */
@Data
public class CartUpdateRequest {

    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private int quantity;
}

