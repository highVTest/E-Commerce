package com.highv.ecommerce.domain.product.dto

data class ReviewResponse(
    val id: Long,
    val productId: Long,
    val reviewerName: String,
    val rating: Int,
    val comment: String

)
