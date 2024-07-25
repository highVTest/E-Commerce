package com.highv.ecommerce.domain.order_master.entity

import com.querydsl.core.annotations.QueryProjection

data class TotalPriceDto @QueryProjection constructor(
    var totalPrice: Int
)