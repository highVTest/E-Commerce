package com.highv.ecommerce.domain.review.repository

import com.highv.ecommerce.domain.review.entity.QReview.review
import com.highv.ecommerce.domain.review.entity.Review
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long>, ReviewQueryDslRepository

interface ReviewQueryDslRepository {
    fun findByIdOrNull(id: Long): Review?
    fun findAllByProductId(productId: Long): List<Review>
    fun findAllByBuyerId(buyerId: Long): List<Review>
    fun findAllByShopId(shopId: Long): List<Review>
}

class ReviewQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ReviewQueryDslRepository {
    override fun findByIdOrNull(id: Long): Review? {
        return queryFactory
            .selectFrom(review)
            .innerJoin(review.product()).fetchJoin()
            .where(review.id.eq(id))
            .fetchOne()
    }

    override fun findAllByProductId(productId: Long): List<Review> {
        return queryFactory
            .selectFrom(review)
            .innerJoin(review.product()).fetchJoin()
            .innerJoin(review.buyer()).fetchJoin()
            .where(review.product().id.eq(productId))
            .fetch()
    }

    override fun findAllByBuyerId(buyerId: Long): List<Review> {
        return queryFactory
            .selectFrom(review)
            .innerJoin(review.product()).fetchJoin()
            .where(review.buyer().id.eq(buyerId))
            .fetch()
    }

    override fun findAllByShopId(shopId: Long): List<Review> {
        return queryFactory
            .selectFrom(review)
            .innerJoin(review.product()).fetchJoin()
            .where(review.product().shop().id.eq(shopId))
            .fetch()
    }
}