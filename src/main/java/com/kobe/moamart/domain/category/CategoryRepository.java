package com.kobe.moamart.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName    : com.kobe.moamart.domain.category
 * fileName       : CategoryRepository
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 루트 카테고리(대분류)만 조회 (Parent가 null인것)
    List<Category> findAllByParentIsNullOrderByDisplayOrderAsc();

    // 특정 부모의 자식 카테고리 조회
    List<Category> findAllByParentIdOrderByDisplayOrderAsc(Long parentId);

    // 카테고리 이름으로 조회 (정확히 일치하는 것)
    Optional<Category> findByName(String name);
}
