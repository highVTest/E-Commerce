package com.highv.ecommerce.domain.review.repository

import com.highv.ecommerce.domain.review.entity.Review

interface ReviewQueryDslRepository {
    fun findByIdOrNull(id: Long): Review?

    fun findAllByProductId(productId: Long): List<Review>

    fun findAllByBuyerId(buyerId: Long): List<Review>
}