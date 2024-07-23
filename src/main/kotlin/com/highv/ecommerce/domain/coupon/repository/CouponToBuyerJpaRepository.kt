package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CouponToBuyerJpaRepository: JpaRepository<CouponToBuyer, Long> {

    fun existsByCouponIdAndBuyerId(couponId: Long, buyerId: Long): Boolean

    @Query("SELECT c FROM CouponToBuyer c JOIN FETCH c.coupon WHERE c.coupon.id = :couponId and c.buyer.id = :buyerId")
    fun findByCouponIdAndBuyerId(couponId: Long, buyerId: Long): CouponToBuyer?

    @Query("select cb from CouponToBuyer cb where cb.coupon.id in :couponId and cb.buyer.id = :buyerId")
    fun findAllByCouponIdAndBuyerId(couponId: List<Long>, buyerId: Long): List<CouponToBuyer>

}