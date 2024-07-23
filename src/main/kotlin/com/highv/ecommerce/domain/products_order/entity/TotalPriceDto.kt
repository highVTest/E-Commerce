package com.highv.ecommerce.domain.products_order.entity

import com.querydsl.core.annotations.QueryProjection

data class TotalPriceDto @QueryProjection constructor(
    var totalPrice: Int
)