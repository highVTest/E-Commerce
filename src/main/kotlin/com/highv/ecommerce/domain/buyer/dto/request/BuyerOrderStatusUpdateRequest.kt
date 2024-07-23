package com.highv.ecommerce.domain.buyer.dto.request

import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType

data class BuyerOrderStatusUpdateRequest(
    val status: OrderStatusType,
    val reason: String
)
