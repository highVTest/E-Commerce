package com.highv.ecommerce.domain.review.repository

import com.highv.ecommerce.domain.review.entity.QReview.review
import com.highv.ecommerce.domain.review.entity.Review
import com.querydsl.jpa.impl.JPAQueryFactory

class ReviewQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ReviewQueryDslRepository {
    override fun findByIdOrNull(id: Long): Review? {
        val query = queryFactory
            .select(review)
            .from(review)
            .innerJoin(review.product()).fetchJoin()
            .innerJoin(review.product().productBackOffice()).fetchJoin()
            .innerJoin(review.product().shop()).fetchJoin()
            .where(review.id.eq(id))
            .fetchOne()

        return query
    }

    override fun findAllByProductId(productId: Long): List<Review> {
        val query = queryFactory
            .select(review)
            .from(review)
            .innerJoin(review.product()).fetchJoin()
            .innerJoin(review.product().productBackOffice()).fetchJoin()
            .innerJoin(review.product().shop()).fetchJoin()
            .innerJoin(review.buyer()).fetchJoin()
            .where(review.product().id.eq(productId))
            .fetch()

        return query
    }

    override fun findAllByBuyerId(buyerId: Long): List<Review> {
        val query = queryFactory
            .select(review)
            .from(review)
            .innerJoin(review.product()).fetchJoin()
            .innerJoin(review.product().productBackOffice()).fetchJoin()
            .innerJoin(review.product().shop()).fetchJoin()
            .where(review.buyer().id.eq(buyerId))
            .fetch()

        return query
    }
}

