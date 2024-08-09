package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import java.time.LocalDateTime

class CouponResponse(
    val couponId: Long,
    val discountPolicy: String,
    val discount : Int,
    val productId : Long,
    val expiredAt : LocalDateTime,
    val quantity : Int,
){
    companion object {
        fun from(coupon: Coupon) = CouponResponse(
            couponId = coupon.id!!,
            discountPolicy = coupon.discountPolicy.name,
            discount = coupon.discount,
            productId = coupon.product.id!!,
            expiredAt = coupon.expiredAt,
            quantity = coupon.quantity,
        )
    }
}
