package com.highv.ecommerce.domain.review.dto

import com.highv.ecommerce.domain.review.entity.Review

data class ReviewResponse(
    val id: Long,
    val rate: Float,
    val content: String,
)
{
    companion object {
        fun from(review: Review)=ReviewResponse(
            id = review.id!!,
            rate = review.rate,
            content = review.content
        )
    }
}
