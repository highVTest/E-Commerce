package com.highv.ecommerce.domain.coupon.repository

import org.springframework.data.jpa.repository.JpaRepository

interface CouponJpaRepository: JpaRepository<Coupon, Long>, CouponRepository{

}