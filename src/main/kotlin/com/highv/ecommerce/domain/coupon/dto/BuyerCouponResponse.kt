package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import java.time.LocalDateTime

class BuyerCouponResponse(
    val couponId: Long,
    val productId: Long,
    val discountPolicy: String,
    val discount : Int,
    val expiredAt : LocalDateTime,
    val couponName : String,
    val isUsed: Boolean,
){
    companion object {
        fun from(couponToBuyer: CouponToBuyer) = BuyerCouponResponse(
            couponId = couponToBuyer.coupon.id!!,
            productId = couponToBuyer.coupon.product.id!!,
            discountPolicy = couponToBuyer.coupon.discountPolicy.name,
            discount = couponToBuyer.coupon.discount,
            expiredAt = couponToBuyer.coupon.expiredAt,
            couponName = couponToBuyer.coupon.couponName,
            isUsed = couponToBuyer.isUsed
        )
    }
}
