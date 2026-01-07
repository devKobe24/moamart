package com.kobe.moamart.service;

import com.kobe.moamart.domain.category.Category;
import com.kobe.moamart.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : com.kobe.moamart.service
 * fileName       : CategoryService
 * author         : kobe
 * date           : 2025. 01. 07.
 * description    : 카테고리 관련 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 01. 07.        kobe       최초 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 대분류 카테고리와 소분류 카테고리를 함께 조회
     * @return 대분류 카테고리 리스트 (각 대분류의 children이 로드됨)
     */
    public List<Category> getParentCategoriesWithChildren() {
        // 대분류 카테고리 조회
        List<Category> parentCategories = categoryRepository.findAllByParentIsNullOrderByDisplayOrderAsc();
        
        // 각 대분류의 children(소분류)를 명시적으로 로드 (LAZY 로딩 초기화)
        for (Category parentCategory : parentCategories) {
            // children 컬렉션에 접근하여 LAZY 로딩 초기화
            List<Category> children = parentCategory.getChildren();
            // size()를 호출하면 LAZY 로딩이 초기화됨
            children.size();
        }
        
        return parentCategories;
    }
}

