package com.highv.ecommerce.domain.product.dto

data class UpdateReviewRequest(
    val rate : Float,
    val content : String
)
