package com.highv.ecommerce.domain.coupon.dto

import java.time.LocalDateTime

data class UpdateCouponRequest(
    val expiredAt: LocalDateTime,
    val discountRate : Int?,
    val discountPrice : Int?,
    val quantity : Int = 0,
)