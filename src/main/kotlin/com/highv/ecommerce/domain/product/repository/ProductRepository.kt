package com.highv.ecommerce.domain.product.repository

import com.highv.ecommerce.domain.backoffice.entity.QProductBackOffice.productBackOffice
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
    // fun findAllByShopId(shopId: Long): List<Product>

    // @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productBackOffice WHERE p.id = :productId")
    // fun findProductWithBackOfficeById(productId: Long): Product?
}

@Repository
interface ProductQueryDslRepository {
    fun findAllPaginated(pageable: Pageable): Page<Product>
    fun findByCategoryPaginated(categoryId: Long, pageable: Pageable): Page<Product>
    fun searchByKeywordPaginated(keyword: String, pageable: Pageable): Page<Product>
    fun findAllById(productIds: Collection<Long>): List<Product>
    fun findByIdOrNull(id: Long): Product?
    fun findAllByShopId(shopId: Long): List<Product>
}

class ProductQueryDslRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ProductQueryDslRepository {

    override fun findAllPaginated(pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .leftJoin(product.productBackOffice(), productBackOffice)
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.productBackOffice(), productBackOffice).fetchJoin()
            .leftJoin(product.shop()).fetchJoin()
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())

        val results = query.fetch()
        return PageImpl(results, pageable, totalCount)
    }

    override fun findByCategoryPaginated(categoryId: Long, pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .leftJoin(product.productBackOffice(), productBackOffice)
            .where(product.categoryId.eq(categoryId))
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.productBackOffice(), productBackOffice).fetchJoin()
            .leftJoin(product.shop()).fetchJoin()
            .where(product.categoryId.eq(categoryId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())

        val results = query.fetch()
        return PageImpl(results, pageable, totalCount)
    }

    override fun searchByKeywordPaginated(keyword: String, pageable: Pageable): Page<Product> {
        val totalCount = jpaQueryFactory
            .select(product.count())
            .from(product)
            .leftJoin(product.productBackOffice(), productBackOffice)
            .where(keywordLike(keyword))
            .fetchOne() ?: 0L

        val query = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.productBackOffice(), productBackOffice).fetchJoin()
            .leftJoin(product.shop()).fetchJoin()
            .where(keywordLike(keyword))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.sort.map { it.toOrderSpecifier() }.toList().toTypedArray())

        val results = query.fetch()
        return PageImpl(results, pageable, totalCount)
    }

    override fun findAllById(productIds: Collection<Long>): List<Product> {
        val query = jpaQueryFactory
            .select(product)
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

    private fun keywordLike(keyword: String): BooleanExpression? {
        return if (keyword.isNotBlank()) {
            product.name.containsIgnoreCase(keyword)
                .or(product.description.containsIgnoreCase(keyword))
        } else {
            null
        }
    }
}