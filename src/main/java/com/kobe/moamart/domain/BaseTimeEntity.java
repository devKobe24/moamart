package com.kobe.moamart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * packageName    : com.kobe.moamart.domain
 * fileName       : BaseTimeEntity
 * author         : kobe
 * date           : 2025. 12. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 25.        kobe       최초 생성
 */
@Getter
@MappedSuperclass // 테이블로 생성되지 않고, 자식 클래스에게 매핑 정보만 제공
@EntityListeners(AuditingEntityListener.class) // Audition 기능 활성화
public class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
