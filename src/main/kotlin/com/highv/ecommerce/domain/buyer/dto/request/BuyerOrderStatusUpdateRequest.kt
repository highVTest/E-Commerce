package com.highv.ecommerce.domain.buyer.dto.request

import com.highv.ecommerce.domain.order_details.enumClass.ComplainType

data class BuyerOrderStatusUpdateRequest(
    val status: ComplainType,
    val reason: String
)
