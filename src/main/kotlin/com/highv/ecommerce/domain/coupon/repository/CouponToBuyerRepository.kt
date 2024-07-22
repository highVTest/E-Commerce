package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import org.springframework.data.jpa.repository.Query

interface CouponToBuyerRepository {

    fun findAllProductIdWithBuyerId(buyerId: Long): List<Long>

    fun existsByCouponIdAndBuyerId(couponId: Long, buyerId: Long): Boolean

    fun save(coupon: CouponToBuyer): CouponToBuyer

    fun findByCouponIdAndBuyerId(couponId: Long, buyerId: Long): CouponToBuyer?

    fun findAllByCouponIdAndBuyerId(couponIdList: List<Long>, buyerId: Long): List<CouponToBuyer>
}