package com.highv.ecommerce.domain.coupon.dto

import java.time.LocalDateTime

data class CreateCouponRequest (
    val productId : Long,
    val discountRate : Int?,
    val discountPrice : Int?,
    val expiredAt: LocalDateTime,
    val quantity : Int
)
