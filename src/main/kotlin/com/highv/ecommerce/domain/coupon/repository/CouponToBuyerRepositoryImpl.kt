package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.entity.QCouponToBuyer
import com.querydsl.jpa.impl.JPADeleteClause
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class CouponToBuyerRepositoryImpl(
    private val couponToBuyerJpaRepository: CouponToBuyerJpaRepository,
    @PersistenceContext
    private val em: EntityManager
) : CouponToBuyerRepository {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)
    private val couponToBuyer = QCouponToBuyer.couponToBuyer

    override fun findAllByBuyerId(buyerId: Long): List<CouponToBuyer> {

        return queryFactory.select(couponToBuyer)
            .from(couponToBuyer)
            .innerJoin(couponToBuyer.coupon()).fetchJoin()
            .where(couponToBuyer.buyerId.eq(buyerId))
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
            .where(couponToBuyer.buyerId.eq(buyerId))
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

    override fun saveAllByCouponIdAndBuyerIdAndIsUsedTrue(coupons: List<Long>, buyerId: Long){

            queryFactory.update(couponToBuyer)
            .set(couponToBuyer.isUsed, false)
            .where(couponToBuyer.coupon().id.`in`(coupons))
            .where(couponToBuyer.buyerId.eq(buyerId))
            .execute()
    }

    override fun delete(couponToBuyer: CouponToBuyer) {
        couponToBuyerJpaRepository.delete(couponToBuyer)
    }

    @Transactional
    override fun deleteAllByExpiredAt() {

        JPADeleteClause(em, couponToBuyer)
            .where(couponToBuyer.coupon().expiredAt.loe(LocalDateTime.now()))
            .execute()
    }
}