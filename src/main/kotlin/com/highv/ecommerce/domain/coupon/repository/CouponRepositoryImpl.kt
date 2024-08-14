package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.QCoupon
import com.highv.ecommerce.domain.product.entity.QProduct
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl(
    private val couponJpaRepository: CouponJpaRepository,
    @PersistenceContext
    private val em: EntityManager
) : CouponRepository {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)
    val coupon: QCoupon = QCoupon.coupon
    val product: QProduct = QProduct.product

    override fun save(coupon: Coupon): Coupon {
        return couponJpaRepository.save(coupon)
    }

    override fun findByIdOrNull(id: Long): Coupon? {
        val query = queryFactory
            .select(coupon)
            .from(coupon)
            .innerJoin(coupon.product()).fetchJoin()
            .innerJoin(coupon.product().productBackOffice()).fetchJoin()
            .innerJoin(coupon.product().shop()).fetchJoin()
            .where(coupon.id.eq(id))
            .fetchOne()

        return query
    }

    override fun delete(coupon: Coupon) {
        couponJpaRepository.delete(coupon)
    }

    override fun findAll(): List<Coupon> {
        return couponJpaRepository.findAll()
    }

    override fun findAllCouponIdWithBuyer(couponIdList: List<Long>): List<Coupon> {

        return queryFactory.selectFrom(coupon)
            .where(coupon.id.`in`(couponIdList))
            .innerJoin(coupon.product(), product).fetchJoin()
            .innerJoin(coupon.product().shop()).fetchJoin()
            .innerJoin(coupon.product().productBackOffice()).fetchJoin()
            .fetch()
    }

    override fun existsByProductId(productId: Long): Boolean {
        return couponJpaRepository.existsByProductId(productId)
    }

    override fun findByIdAndSellerId(couponId: Long, sellerId: Long): Coupon? {
        val query = queryFactory
            .select(coupon)
            .from(coupon)
            .innerJoin(coupon.product()).fetchJoin()
            .innerJoin(coupon.product().productBackOffice()).fetchJoin()
            .innerJoin(coupon.product().shop()).fetchJoin()
            .where(coupon.id.eq(couponId))
            .where(coupon.sellerId.eq(sellerId))
            .fetchOne()

        return query
    }

    override fun findAllBySellerId(sellerId: Long): List<Coupon> {
        val query = queryFactory
            .select(coupon)
            .from(coupon)
            .innerJoin(coupon.product()).fetchJoin()
            .innerJoin(coupon.product().productBackOffice()).fetchJoin()
            .innerJoin(coupon.product().shop()).fetchJoin()
            .where(coupon.sellerId.eq(sellerId))
            .fetch()

        return query
    }


    override fun findAllByProductId(productIdList: List<Long>): List<Long> {
        return couponJpaRepository.findAllByProductIdList(productIdList)
    }

    override fun flush() {
        couponJpaRepository.flush()
    }

    override fun saveAndFlush(coupon: Coupon) {
        couponJpaRepository.saveAndFlush(coupon)
    }

    override fun findByProductId(productId: Long): Coupon? {
        return couponJpaRepository.findByProductId(productId)
    }

    override fun deleteAllByExpiredAt(){
        couponJpaRepository.deleteAllByExpiredAt()
    }
}