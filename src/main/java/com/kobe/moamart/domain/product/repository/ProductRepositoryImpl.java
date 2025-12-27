package com.kobe.moamart.domain.product.repository;

import com.kobe.moamart.domain.product.entity.Product;
import com.kobe.moamart.domain.product.entity.ProductStatus;
import com.kobe.moamart.dto.request.ProductSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.kobe.moamart.domain.category.QCategory.category;
import static com.kobe.moamart.domain.product.entity.QProduct.product;

/**
 * packageName    : com.kobe.moamart.domain.product.repository
 * fileName       : ProductRepositoryImpl
 * author         : kobe
 * date           : 2025. 12. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 26.        kobe       최초 생성
 */

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Product> search(ProductSearchCondition condition, Pageable pageable) {

        // 1. 컨텐츠 조회 (데이터)
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin() // 성능 최적화: 카테고리 한방 쿼리
                .where(
                        eqCategory(condition.getCategoryId()),
                        containKeyword(condition.getKeyword()),
                        eqStatus(condition.getStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.id.desc()) // 최신순 정렬
                .fetch();

        // 2. 카운트 쿼리 (페이징용)
        // 성능 팁: 카운트 쿼리는 조인이 필요 없는 경우에는 조인을 빼는 것이 좋음
        JPAQuery<Long> countQuery = queryFactory
                .select(product.id)
                .from(product)
                .where(
                        eqCategory(condition.getCategoryId()),
                        containKeyword(condition.getKeyword()),
                        eqStatus(condition.getStatus())
                );

        // 3. Page 객체로 변환 (Spring Data Support)
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // --- 동적 쿼리 조건들 (BooleanExpression) ---
    // null을 반환하면 QueryDSL이 알아서 where 절에서 제거해줍니다.
    private BooleanExpression eqCategory(Long categoryId) {
        return categoryId != null ? product.category.id.eq(categoryId) : null;
    }

    private BooleanExpression containKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? product.name.contains(keyword) : null;
    }

    private BooleanExpression eqStatus(ProductStatus status) {
        return status != null ? product.status.eq(status) : null;
    }
}
