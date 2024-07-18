package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.entity.QCouponToBuyer
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class CouponToBuyerRepositoryImpl(
    private val couponToBuyerJpaRepository: CouponToBuyerJpaRepository,
    @PersistenceContext
    private val em : EntityManager
): CouponToBuyerRepository {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)
    private val couponToBuyer = QCouponToBuyer.couponToBuyer

    override fun findAllProductIdWithBuyerId(buyerId: Long): List<Long> {

        return queryFactory.select(couponToBuyer.coupon().id)
            .from(couponToBuyer)
            .where(couponToBuyer.buyer().id.eq(buyerId))
            .fetch()

    }

    override fun existsByCouponIdAndBuyerId(couponId: Long, buyerId: Long): Boolean {
       return couponToBuyerJpaRepository.existsByCouponIdAndBuyerId(couponId, buyerId)
    }

    override fun save(coupon: CouponToBuyer): CouponToBuyer {
        return couponToBuyerJpaRepository.save(coupon)
    }
}