package com.kobe.moamart.domain.store.repository;

import com.kobe.moamart.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.kobe.moamart.domain.store.repository
 * fileName       : StoreRepository
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 매장 Repository
 */
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    // 활성화된 매장 목록 조회
    List<Store> findByIsActiveTrueOrderByIdAsc();
    
    // 매장명으로 조회
    Optional<Store> findByName(String name);
}
