package com.highv.ecommerce.domain.review.repository

import com.highv.ecommerce.domain.product.entity.QProduct.product
import com.highv.ecommerce.domain.review.entity.QReview.review
import com.highv.ecommerce.domain.review.entity.Review
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long>, ReviewQueryDslRepository {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Review r SET r.rate = :rate , r.content = :content WHERE r.id = :reviewId and r.buyer.id = :buyerId")
    fun updateByReviewIdAndBuyerId(reviewId: Long, buyerId: Long, rate: Float, content: String)

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Review r WHERE r.id = :reviewId and r.buyer.id = :buyerId")
    fun deleteByReviewIdAndBuyerId(reviewId: Long, buyerId: Long)

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Review r WHERE r.productId = :productId")
    fun deleteByProductId(productId: Long)
}

interface ReviewQueryDslRepository {
    fun findByIdOrNull(id: Long): Review?
    fun findAllByProductId(productId: Long): List<Review>
    fun findAllByBuyerId(buyerId: Long): List<Review>
    fun findAllByShopId(shopId: Long): List<Float>
}

class ReviewQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ReviewQueryDslRepository {
    override fun findByIdOrNull(id: Long): Review? {
        return queryFactory
            .selectFrom(review)
            .where(review.id.eq(id))
            .fetchOne()
    }

    override fun findAllByProductId(productId: Long): List<Review> {
        return queryFactory
            .selectFrom(review)
            .innerJoin(review.buyer()).fetchJoin()
            .where(review.productId.eq(productId))
            .fetch()
    }

    override fun findAllByBuyerId(buyerId: Long): List<Review> {
        return queryFactory
            .selectFrom(review)
            .where(review.buyer().id.eq(buyerId))
            .fetch()
    }

    override fun findAllByShopId(shopId: Long): List<Float> {

        val productIdList = queryFactory.select(
            product.id
        )
            .from(product)
            .where(product.shop().id.eq(shopId))
            .fetch()

        return queryFactory
            .select(review.rate)
            .from(review)
            .where(review.productId.`in`(productIdList))
            .fetch()
    }
}