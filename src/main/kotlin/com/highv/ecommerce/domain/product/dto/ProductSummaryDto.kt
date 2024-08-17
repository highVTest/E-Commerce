package com.highv.ecommerce.domain.product.dto

import com.querydsl.core.annotations.QueryProjection
import java.io.Serializable

data class ProductSummaryDto @QueryProjection constructor(
    val id: Long,
    val image: String,
    val name: String,
    val price: Int,
) : Serializable