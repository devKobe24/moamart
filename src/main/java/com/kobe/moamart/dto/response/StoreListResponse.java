package com.kobe.moamart.dto.response;

import com.kobe.moamart.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * packageName    : com.kobe.moamart.dto.response
 * fileName       : StoreListResponse
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 매장 목록 응답 DTO
 */
@Getter
public class StoreListResponse {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StoreListResponse(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.address = store.getAddress();
        this.phoneNumber = store.getPhoneNumber();
        this.isActive = store.isActive();
        this.createdAt = store.getCreatedAt();
        this.updatedAt = store.getUpdatedAt();
    }
}

