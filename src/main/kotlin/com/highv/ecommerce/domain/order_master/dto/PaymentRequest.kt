package com.highv.ecommerce.domain.order_master.dto

data class PaymentRequest(
    val cartIdList : ArrayList<Long>,
    val couponIdList: ArrayList<Long>
)