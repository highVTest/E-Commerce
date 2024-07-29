package com.highv.ecommerce.domain.review.repository

import com.highv.ecommerce.domain.review.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository: JpaRepository<Review, Long> {
    fun findAllByProductId(productId: Long): List<Review>
    fun findAllByBuyerId(buyerId: Long): List<Review>
}