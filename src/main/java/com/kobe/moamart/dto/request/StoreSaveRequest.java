package com.kobe.moamart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * packageName    : com.kobe.moamart.dto.request
 * fileName       : StoreSaveRequest
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 매장 저장/수정 요청 DTO
 */
@Data
public class StoreSaveRequest {
    
    @NotBlank(message = "매장명을 입력해주세요.")
    private String name;
    
    @NotBlank(message = "주소를 입력해주세요.")
    private String address;
    
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phoneNumber;
    
    private boolean isActive = true;
}

