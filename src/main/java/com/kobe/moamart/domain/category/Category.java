package com.kobe.moamart.domain.category;

import com.kobe.moamart.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.kobe.moamart.domain.category
 * fileName       : Category
 * author         : kobe
 * date           : 2025. 12. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 25.        kobe       최초 생성
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    private int depth; // 1: 대분류, 2: 소분류

    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @Builder
    public Category(String name, int depth, int displayOrder, Category parent) {
        this.name = name;
        this.depth = depth;
        this.displayOrder = displayOrder;
        this.parent = parent;
    }

    /**
     * 소분류인지 확인 (상품 등록 가능한 카테고리인지 확인)
     * @return depth가 2이면 true
     */
    public boolean isSubCategory() {
        return this.depth == 2;
    }

    /**
     * 대분류인지 확인
     * @return depth가 1이면 true
     */
    public boolean isMainCategory() {
        return this.depth == 1;
    }
}
