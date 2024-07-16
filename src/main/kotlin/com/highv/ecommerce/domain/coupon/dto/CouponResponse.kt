package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.entity.Coupon
import java.time.LocalDateTime

class CouponResponse(
    val discountRate : Int?,
    val discountPrice : Int?,
    val productId : Long,
    val expiredAt : LocalDateTime
){
    companion object {
        fun from(coupon: Coupon) = CouponResponse(
            discountRate = coupon.discountRate,
            discountPrice = coupon.discountPrice,
            productId = coupon.product.id!!,
            expiredAt = coupon.expiredAt
        )
    }
}
