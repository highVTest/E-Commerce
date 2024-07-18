package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import java.time.LocalDateTime

data class UpdateCouponRequest(
    val expiredAt: LocalDateTime,
    val discountPolicy: DiscountPolicy,
    val discount: Int,
    val quantity : Int = 0,
)