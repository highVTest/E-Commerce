package com.highv.ecommerce.domain.order_master.dto

import com.querydsl.core.annotations.QueryProjection

data class TotalPriceDto @QueryProjection constructor(
    val itemCartId : Long,
    val price : Int
)