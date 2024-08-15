package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.entity.Coupon
import java.time.LocalDateTime

class SellerCouponResponse(
    val couponId: Long,
    val discountPolicy: String,
    val discount : Int,
    val productId : Long,
    val expiredAt : LocalDateTime,
    val quantity : Int,
    val couponName : String,
){
    companion object {
        fun from(coupon: Coupon) = SellerCouponResponse(
            couponId = coupon.id!!,
            discountPolicy = coupon.discountPolicy.name,
            discount = coupon.discount,
            productId = coupon.product.id!!,
            expiredAt = coupon.expiredAt,
            quantity = coupon.quantity,
            couponName = coupon.couponName,
        )
    }
}
