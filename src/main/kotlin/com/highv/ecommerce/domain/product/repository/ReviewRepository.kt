package com.highv.ecommerce.domain.product.repository

import com.highv.ecommerce.domain.product.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository: JpaRepository<Review, Long> {
    fun findAllByProductId(productId: Long): List<Review>
    fun findByProductIdAndBuyerId(productId: Long, buyerId: Long): Review?
}