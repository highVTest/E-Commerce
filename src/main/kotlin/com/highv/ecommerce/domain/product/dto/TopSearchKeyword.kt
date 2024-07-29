package com.highv.ecommerce.domain.product.dto

import java.io.Serializable

data class TopSearchKeyword(
    val keyword: String?,
    val score: Double?
) : Serializable