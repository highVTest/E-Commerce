package com.highv.ecommerce.domain.coupon.dto

import java.time.LocalDateTime

class CouponResponse(
    val sellerId: Long,
    val discountRate : Int?,
    val discountPrice : Int?,
    val produceId : Long,
    val expiredAt : LocalDateTime
){
    companion object {
        fun from(coupon: Coupon) = CouponResponse(
            sellerId = coupon.sellerId,
            discountRate = coupon.discountRate,
            discountPrice = coupon.discountPrice,
            produceId = coupon.produceId,
            expiredAt = coupon.expiredAt
        )
    }
}
