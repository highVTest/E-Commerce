package com.highv.ecommerce.domain.product.dto

import com.querydsl.core.annotations.QueryProjection
import org.ietf.jgss.Oid

data class ReviewProductDto @QueryProjection constructor(
    val productId: Long,
    val productName: String,
    val productImage: String,
)
