package com.kobe.moamart.domain.store.entity;

import com.kobe.moamart.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.kobe.moamart.domain.store.entity
 * fileName       : Store
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 매장 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "store")
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false)
    private String name; // 매장명

    @Column(nullable = false)
    private String address; // 매장 주소

    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @Column(nullable = false)
    private boolean isActive; // 운영 여부

    @Builder
    public Store(String name, String address, String phoneNumber, boolean isActive) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
    }

    // 매장 정보 수정
    public void updateInfo(String name, String address, String phoneNumber, boolean isActive) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
    }
}
