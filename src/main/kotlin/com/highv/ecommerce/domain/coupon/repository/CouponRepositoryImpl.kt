package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl(
    private val couponJpaRepository: CouponJpaRepository
): CouponRepository {

    override fun save(coupon: Coupon): Coupon {
        return couponJpaRepository.save(coupon)
    }

    override fun findByIdOrNull(id: Long): Coupon? {
        return couponJpaRepository.findByIdOrNull(id)
    }

    override fun delete(coupon: Coupon) {
        couponJpaRepository.delete(coupon)
    }

    override fun findAll(): List<Coupon> {
        return couponJpaRepository.findAll()
    }
}