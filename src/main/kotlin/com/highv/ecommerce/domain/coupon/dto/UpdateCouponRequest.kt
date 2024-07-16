package com.highv.ecommerce.domain.coupon.dto

data class UpdateCouponRequest(
    val productId : Long,
    val discountRate : Int?,
    val discountPrice : Int?,
)
