package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.entity.QCoupon.coupon
import com.highv.ecommerce.domain.coupon.entity.QCouponToBuyer
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class CouponToBuyerRepositoryImpl(
    private val couponToBuyerJpaRepository: CouponToBuyerJpaRepository,
    @PersistenceContext
    private val em: EntityManager
) : CouponToBuyerRepository {

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

    override fun findByProductIdAndBuyerId(productId: Long, buyerId: Long): CouponToBuyer? {
        val query = queryFactory
            .select(couponToBuyer)
            .from(couponToBuyer)
            .innerJoin(couponToBuyer.coupon()).fetchJoin()
            .innerJoin(couponToBuyer.coupon().product()).fetchJoin()
            .innerJoin(couponToBuyer.coupon().product().productBackOffice()).fetchJoin()
            .innerJoin(couponToBuyer.coupon().product().shop()).fetchJoin()
            .innerJoin(couponToBuyer.buyer()).fetchJoin()
            .where(couponToBuyer.buyer().id.eq(buyerId))
            .where(couponToBuyer.coupon().product().id.eq(productId))
            .fetchOne()

        return query
    }

    override fun findAllByCouponIdAndBuyerIdAndIsUsedFalse(
        couponIdList: List<Long>,
        buyerId: Long
    ): List<CouponToBuyer> {
        return couponToBuyerJpaRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(couponIdList, buyerId)
    }

    override fun deleteAll(couponToBuyerList: List<CouponToBuyer>) {
        return couponToBuyerJpaRepository.deleteAll(couponToBuyerList)
    }

    override fun findByCouponIdAndBuyerIdAndIsUsedFalse(couponId: Long, buyerId: Long): CouponToBuyer? {
        return couponToBuyerJpaRepository.findByCouponIdAndBuyerIdAndIsUsedFalse(couponId, buyerId)
    }

    override fun findAllByCouponIdAndBuyerIdAndIsUsedTrue(coupons: List<Long>, buyerId: Long): List<CouponToBuyer> {
        return couponToBuyerJpaRepository.findAllByCouponIdAndBuyerIdAndIsUsedTrue(coupons, buyerId)
    }
}