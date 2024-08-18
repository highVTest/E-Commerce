package com.highv.ecommerce.domain.review.dto

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.product.dto.ReviewProductDto
import com.highv.ecommerce.domain.review.entity.Review

data class BuyerReviewResponse(
    val id: Long,
    val productId: Long,
    val rate: Float,
    val content: String,
    val buyerName: String,
    val buyerProfileImage: String,
    val productName: String,
    val productImage: String,
)
{
    companion object {
        fun from(review: Review, reviewProductDto: ReviewProductDto)=BuyerReviewResponse(
            id = review.id!!,
            productId = review.productId,
            rate = review.rate,
            content = review.content,
            buyerName = review.buyer.nickname,
            buyerProfileImage = review.buyer.profileImage,
            productName = reviewProductDto.productName,
            productImage = reviewProductDto.productImage,
        )
    }
}
