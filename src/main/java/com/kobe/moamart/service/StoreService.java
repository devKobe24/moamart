package com.kobe.moamart.service;

import com.kobe.moamart.domain.store.entity.Store;
import com.kobe.moamart.domain.store.repository.StoreRepository;
import com.kobe.moamart.dto.request.StoreSaveRequest;
import com.kobe.moamart.dto.response.StoreListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.kobe.moamart.service
 * fileName       : StoreService
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 매장 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    /**
     * 모든 매장 목록 조회
     */
    public List<StoreListResponse> getAllStores() {
        return storeRepository.findAll().stream()
                .map(StoreListResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 매장 목록 조회
     */
    public List<StoreListResponse> getActiveStores() {
        return storeRepository.findByIsActiveTrueOrderByIdAsc().stream()
                .map(StoreListResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 매장 조회
     */
    public Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다. id=" + id));
    }

    /**
     * 매장 저장
     */
    @Transactional
    public Long saveStore(StoreSaveRequest request) {
        Store store = Store.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .isActive(request.isActive())
                .build();
        
        return storeRepository.save(store).getId();
    }

    /**
     * 매장 수정
     */
    @Transactional
    public void updateStore(Long id, StoreSaveRequest request) {
        Store store = getStore(id);
        store.updateInfo(
                request.getName(),
                request.getAddress(),
                request.getPhoneNumber(),
                request.isActive()
        );
    }

    /**
     * 매장 삭제
     */
    @Transactional
    public void deleteStore(Long id) {
        Store store = getStore(id);
        storeRepository.delete(store);
    }
}

