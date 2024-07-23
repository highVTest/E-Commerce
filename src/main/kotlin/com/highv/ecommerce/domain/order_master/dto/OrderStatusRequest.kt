package com.highv.ecommerce.domain.order_master.dto

import com.highv.ecommerce.domain.order_master.enumClass.StatusCode

data class OrderStatusRequest(
    val statusCode: StatusCode
)