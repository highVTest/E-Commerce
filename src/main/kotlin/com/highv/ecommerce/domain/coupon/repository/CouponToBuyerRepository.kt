package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import org.springframework.data.jpa.repository.JpaRepository

interface CouponToBuyerRepository: JpaRepository<CouponToBuyer, Long> {
}