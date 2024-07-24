package com.highv.ecommerce.domain.order_master.repository

import com.highv.ecommerce.domain.coupon.entity.QCoupon
import com.highv.ecommerce.domain.coupon.entity.QCouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.item_cart.entity.QItemCart
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
    private val em: EntityManager
): OrderMasterRepository {

    private val queryFactory = JPAQueryFactory(em)
    private val itemCart = QItemCart.itemCart
    private val couponToBuyer = QCouponToBuyer.couponToBuyer
    private val coupon = QCoupon.coupon

    override fun saveAndFlush(productsOrder: OrderMaster): OrderMaster {
        return productsOrderJpaRepository.saveAndFlush(productsOrder)
    }

    override fun findByIdOrNull(Id: Long): OrderMaster? {
        return productsOrderJpaRepository.findByIdOrNull(Id)
    }

    override fun save(productsOrder: OrderMaster): OrderMaster {
        return productsOrderJpaRepository.save(productsOrder)
    }

    // TODO("수정 필요")
    override fun discountTotalPriceList(buyerId: Long, couponIdList: List<Long>): Int {

        val query = queryFactory.select(
            itemCart
//                getTotalPrice().sum()
        ).from(itemCart)
            .leftJoin(couponToBuyer).fetchJoin()
            .on(couponToBuyer.buyer().id.eq(itemCart.buyerId)
                .and(couponToBuyer.coupon().id.`in`(couponIdList)))
            .leftJoin(coupon).fetchJoin()
            .on(coupon.id.eq(couponToBuyer.coupon().id)
                .and(coupon.product().id.eq(itemCart.product().id)))
            .where(itemCart.buyerId.eq(buyerId))
            .fetchOne()


        return 1
    }

//    private fun getTotalPrice(): NumberExpression<Int>{
//
//        return CaseBuilder()
//            .`when`(couponToBuyer.coupon().id.isNotNull
//                .and(coupon.discountPolicy.eq(DiscountPolicy.DISCOUNT_RATE)))
//            .then(itemCart.price.multiply(itemCart.quantity)
//                .subtract(itemCart.price.multiply(itemCart.quantity).multiply(coupon.discount.divide(100.0))))
//            .`when`(couponToBuyer.coupon().id.isNotNull
//                .and(coupon.discountPolicy.eq(DiscountPolicy.DISCOUNT_PRICE)))
//            .then(itemCart.price.multiply(itemCart.quantity)
//                .subtract(coupon.discount))
//            .otherwise(itemCart.price.multiply(itemCart.quantity))
//    }
}


