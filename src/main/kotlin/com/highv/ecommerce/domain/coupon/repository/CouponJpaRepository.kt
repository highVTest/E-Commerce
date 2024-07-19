package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponJpaRepository: JpaRepository<Coupon, Long>{

    fun existsByProductId(productId: Long): Boolean

    fun findByIdAndSellerId(id: Long, sellerId: Long): Coupon?

    fun findAllBySellerId(sellerId: Long): List<Coupon>
}