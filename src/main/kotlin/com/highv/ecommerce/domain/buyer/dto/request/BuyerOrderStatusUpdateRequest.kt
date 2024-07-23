package com.highv.ecommerce.domain.buyer.dto.request

import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class BuyerOrderStatusUpdateRequest(
    val status: OrderStatus,
    val reason: String
)
