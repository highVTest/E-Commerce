package com.highv.ecommerce.domain.order_master.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.entity.QCoupon
import com.highv.ecommerce.domain.coupon.entity.QCouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.order_master.dto.QTotalPriceDto
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class OrderMasterRepositoryImpl(
    private val productsOrderJpaRepository: OrderMasterJpaRepository,
    @PersistenceContext
    private val em: EntityManager,
    private val orderMasterJpaRepository: OrderMasterJpaRepository
) : OrderMasterRepository {

    private val queryFactory = JPAQueryFactory(em)
    private val itemCart = QItemCart.itemCart
    private val couponToBuyer = QCouponToBuyer.couponToBuyer
    private val coupon = QCoupon.coupon

    override fun saveAndFlush(productsOrder: OrderMaster): OrderMaster {
        return productsOrderJpaRepository.saveAndFlush(productsOrder)
    }

    override fun findByIdOrNull(id: Long): OrderMaster? {
        return productsOrderJpaRepository.findByIdOrNull(id)
    }

    override fun save(productsOrder: OrderMaster): OrderMaster {
        return productsOrderJpaRepository.save(productsOrder)
    }

    override fun discountTotalPriceList(buyerId: Long, couponIdList: List<CouponToBuyer>): Map<Long, Int> {

        val query = queryFactory.select(
            QTotalPriceDto(
                itemCart.id,
                getTotalPrice()
            )
        ).from(itemCart)
            .leftJoin(couponToBuyer).fetchJoin()
            .on(
                couponToBuyer.buyerId.eq(itemCart.buyer().id)
                    .and(couponToBuyer.coupon().id.`in`(couponIdList.map { it.id }))
            )
            .leftJoin(coupon).fetchJoin()
            .on(
                coupon.id.eq(couponToBuyer.coupon().id)
                    .and(coupon.product().id.eq(itemCart.product().id))
            )
            .where(itemCart.buyer().id.eq(buyerId))
            .groupBy(itemCart.buyer().id, itemCart.product().id)
            .fetch()


        return query.associate { it.itemCartId to it.price }
    }

    override fun findByIdIn(ids: List<Long>): List<OrderMaster> {
        return productsOrderJpaRepository.findByIdIn(ids)
    }

    override fun findByIdInOrderByIdDesc(ids: Set<Long>): List<OrderMaster> {
        return orderMasterJpaRepository.findByIdInOrderByIdDesc(ids)
    }

    private fun getTotalPrice(): NumberExpression<Int> {

        return CaseBuilder()
            .`when`(
                couponToBuyer.coupon().id.isNotNull()
                    .and(coupon.discountPolicy.eq(DiscountPolicy.DISCOUNT_RATE))
            )
            .then(
                itemCart.product().productBackOffice().price.multiply(itemCart.quantity)
                    .subtract(
                        itemCart.product().productBackOffice().price.multiply(itemCart.quantity)
                            .multiply(coupon.discount.divide(100.0))
                    )
            )
            .`when`(
                couponToBuyer.coupon().id.isNotNull()
                    .and(coupon.discountPolicy.eq(DiscountPolicy.DISCOUNT_PRICE))
            )
            .then(
                itemCart.product().productBackOffice().price.multiply(itemCart.quantity)
                    .subtract(coupon.discount)
            )
            .otherwise(itemCart.product().productBackOffice().price.multiply(itemCart.quantity))
    }
}


