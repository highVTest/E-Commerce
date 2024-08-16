package com.highv.ecommerce.domain.review.dto

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.review.entity.Review

data class ReviewResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productImage: String,
    val rate: Float,
    val content: String,
    val buyerName: String,
    val buyerProfileImage: String,
)
{
    companion object {
        fun from(review: Review)=ReviewResponse(
            id = review.id!!,
            rate = review.rate,
            content = review.content,
            productImage = review.product.productImage,
            productName = review.product.name,
            productId = review.product.id!!,
            buyerName = review.buyer.nickname,
            buyerProfileImage = review.buyer.profileImage

        )
    }
}
