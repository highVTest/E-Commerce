package com.highv.ecommerce.domain.product.repository

import com.highv.ecommerce.domain.backoffice.entity.QProductBackOffice.productBackOffice
import com.highv.ecommerce.domain.product.dto.ProductSummaryDto
import com.highv.ecommerce.domain.product.dto.QProductSummaryDto
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.entity.QProduct.product
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>, ProductQueryDslRepository {
    fun existsByNameAndShopId(name: String, shopId: Long): Boolean
}

@Repository
interface ProductQueryDslRepository {
    fun findAllPaginated(pageable: Pageable): Page<ProductSummaryDto>
    fun findByCategoryPaginated(categoryId: Long, pageable: Pageable): Page<ProductSummaryDto>
    fun searchByKeywordPaginated(keyword: String, pageable: Pageable): Page<ProductSummaryDto>
    fun findAllById(productIds: Collection<Long>): List<ProductSummaryDto>
    fun findByIdOrNull(id: Long): Product?
    fun findAllByShopId(shopId: Long): List<Product>
    fun findPaginatedByShopId(shopId: Long, pageable: Pageable): Page<Product>
}

class ProductQueryDslRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ProductQueryDslRepository {

    override fun findAllPaginated(pageable: Pageable): Page<ProductSummaryDto> {
        val results = jpaQueryFactory
            .select(
                QProductSummaryDto(
                    product.id,
                    product.productImage,
                    product.name,
                    product.productBackOffice().price,
                )
            )
            .from(product)
            .where(product.isDeleted.eq(false))
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
        val totalCount = results.size.toLong()

        return PageImpl(results, pageable, totalCount)
    }

    override fun findByCategoryPaginated(categoryId: Long, pageable: Pageable): Page<ProductSummaryDto> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .where(product.categoryId.eq(categoryId))
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .select(
                QProductSummaryDto(
                    product.id,
                    product.productImage,
                    product.name,
                    product.productBackOffice().price,
                )
            ).from(product)
            .leftJoin(product.shop()).fetchJoin()
            .where(product.categoryId.eq(categoryId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())

        val results = query.fetch()

        return PageImpl(results, pageable, totalCount)
    }

    override fun searchByKeywordPaginated(keyword: String, pageable: Pageable): Page<ProductSummaryDto> {
        val results = jpaQueryFactory
            .select(
                QProductSummaryDto(
                    product.id,
                    product.productImage,
                    product.name,
                    product.productBackOffice().price,
                )
            ).from(product)
            .leftJoin(product.shop()).fetchJoin()
            .leftJoin(product.productBackOffice()).fetchJoin()
            .where(product.isDeleted.eq(false).and(keywordLike(keyword)))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())
            .fetch()

        val totalCount = results.size.toLong()
        return PageImpl(results, pageable, totalCount)
    }

    override fun findAllById(productIds: Collection<Long>): List<ProductSummaryDto> {
        val query = jpaQueryFactory
            .select(
                QProductSummaryDto(
                    product.id,
                    product.productImage,
                    product.name,
                    product.productBackOffice().price,
                )
            )
            .from(product)
            .innerJoin(product.productBackOffice()).fetchJoin()
            .innerJoin(product.shop()).fetchJoin()
            .where(product.id.`in`(productIds.toList()))
            .fetch()

        return query
    }

    override fun findByIdOrNull(id: Long): Product? {
        val query = jpaQueryFactory
            .select(product)
            .from(product)
            .innerJoin(product.productBackOffice()).fetchJoin()
            .innerJoin(product.shop()).fetchJoin()
            .where(product.id.eq(id))
            .fetchOne()

        return query
    }

    override fun findAllByShopId(shopId: Long): List<Product> {
        val query = jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.productBackOffice()).fetchJoin()
            .innerJoin(product.shop()).fetchJoin()
            .where(product.shop().sellerId.eq(shopId))
            .fetch()

        return query
    }

    override fun findPaginatedByShopId(shopId: Long, pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .innerJoin(product.productBackOffice(), productBackOffice)
            .innerJoin(product.shop())
            .where(product.shop().id.eq(shopId))
            .fetchOne() ?: 0L

        val results = jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.productBackOffice(), productBackOffice).fetchJoin()
            .innerJoin(product.shop()).fetchJoin()
            .where(product.shop().id.eq(shopId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }

    private fun keywordLike(keyword: String): BooleanExpression? {
        return if (keyword.isNotBlank()) {
            product.name.containsIgnoreCase(keyword)
        } else {
            null
        }
    }
}