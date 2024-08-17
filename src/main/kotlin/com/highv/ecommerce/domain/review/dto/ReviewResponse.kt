package com.highv.ecommerce.domain.review.dto

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.review.entity.Review

data class ReviewResponse(
    val id: Long,
    val productId: Long,
    val rate: Float,
    val content: String,
    val buyerName: String,
    val buyerProfileImage: String,
)
{
    companion object {
        fun from(review: Review)=ReviewResponse(
            id = review.id!!,
            productId = review.productId,
            rate = review.rate,
            content = review.content,
            buyerName = review.buyer.nickname,
            buyerProfileImage = review.buyer.profileImage

        )
    }
}
