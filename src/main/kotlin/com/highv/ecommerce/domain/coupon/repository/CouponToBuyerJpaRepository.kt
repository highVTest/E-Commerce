package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CouponToBuyerJpaRepository: JpaRepository<CouponToBuyer, Long> {

    fun existsByCouponIdAndBuyerId(couponId: Long, buyerId: Long): Boolean


    @Query("select cb from CouponToBuyer cb where cb.coupon.id in :couponId and cb.buyerId = :buyerId and cb.isUsed = false")
    fun findAllByCouponIdAndBuyerIdAndIsUsedFalse(couponId: List<Long>, buyerId: Long): List<CouponToBuyer>

    fun findByCouponIdAndBuyerIdAndIsUsedFalse(couponId: Long, buyerId: Long): CouponToBuyer?

}