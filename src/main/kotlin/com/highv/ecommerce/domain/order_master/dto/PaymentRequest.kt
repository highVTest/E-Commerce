package com.highv.ecommerce.domain.order_master.dto

data class PaymentRequest(
    val cartIdList: List<Long>,
    val couponIdList: List<Long>
)