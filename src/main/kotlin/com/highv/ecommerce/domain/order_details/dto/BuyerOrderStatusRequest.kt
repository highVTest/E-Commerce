package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.enumClass.ComplainType

data class BuyerOrderStatusRequest(
    val complainType: ComplainType,
    val description: String
)