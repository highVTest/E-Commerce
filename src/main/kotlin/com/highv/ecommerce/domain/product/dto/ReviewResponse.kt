package com.highv.ecommerce.domain.product.dto

import com.highv.ecommerce.domain.product.entity.Product

data class ReviewResponse(
    val id: Long,
    val rate: Float,
    val content: String,

)
