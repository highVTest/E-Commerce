package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon


interface CouponRepository {

    fun save(coupon: Coupon): Coupon

    fun findByIdOrNull(id: Long): Coupon?

    fun delete(coupon: Coupon)

    fun findAll(): List<Coupon>
}