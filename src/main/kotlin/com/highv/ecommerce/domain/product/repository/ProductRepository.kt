package com.highv.ecommerce.domain.product.repository

import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.entity.QProduct
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>, ProductQueryDslRepository {
    fun findAllByShopId(shopId: Long): List<Product>
}

interface ProductQueryDslRepository {
    fun findAllPaginated(pageable: Pageable): Page<Product>
    fun findByCategoryPaginated(categoryId: Long, pageable: Pageable): Page<Product>
    fun searchByKeywordPaginated(keyword: String, pageable: Pageable): Page<Product>
}

class ProductQueryDslRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ProductQueryDslRepository {
    private val product = QProduct.product
    override fun findAllPaginated(pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .selectFrom(product)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val results = query.fetch()
        return PageImpl(results, pageable, totalCount)
    }

    override fun findByCategoryPaginated(categoryId: Long, pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .where(product.categoryId.eq(categoryId))
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .selectFrom(product)
            .where(product.categoryId.eq(categoryId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val results = query.fetch()
        return PageImpl(results, pageable, totalCount)
    }

    override fun searchByKeywordPaginated(keyword: String, pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .selectFrom(product)
            .where(keywordLike(keyword))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val results = query.fetch()
        return PageImpl(results, pageable, totalCount)
    }

    private fun keywordLike(keyword: String?): BooleanExpression? {
        return if (keyword.isNullOrEmpty()) null else product.name.contains(keyword)
    }
}