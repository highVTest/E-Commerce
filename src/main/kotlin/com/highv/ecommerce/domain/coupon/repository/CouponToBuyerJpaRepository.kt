package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import org.springframework.data.jpa.repository.JpaRepository

interface CouponToBuyerJpaRepository: JpaRepository<CouponToBuyer, Long> {

    fun existsByCouponIdAndBuyerId(couponId: Long, buyerId: Long): Boolean

}