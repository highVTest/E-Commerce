package com.highv.ecommerce.domain.products_order.repository

import com.highv.ecommerce.domain.coupon.entity.QCoupon
import com.highv.ecommerce.domain.coupon.entity.QCouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.entity.QProductsOrder.productsOrder
import com.highv.ecommerce.domain.products_order.entity.QTotalPriceDto
import com.highv.ecommerce.domain.products_order.entity.TotalPriceDto
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ProductsOrderRepositoryImpl(
    private val productsOrderJpaRepository: ProductsOrderJpaRepository,
    @PersistenceContext
    private val em: EntityManager
): ProductsOrderRepository {

    private val queryFactory = JPAQueryFactory(em)
    private val itemCart = QItemCart.itemCart
    private val couponToBuyer = QCouponToBuyer.couponToBuyer
    private val coupon = QCoupon.coupon

    override fun saveAndFlush(productsOrder: ProductsOrder): ProductsOrder {
        return productsOrderJpaRepository.saveAndFlush(productsOrder)
    }

    override fun findByIdOrNull(Id: Long): ProductsOrder? {
        return productsOrderJpaRepository.findByIdOrNull(Id)
    }

    override fun save(productsOrder: ProductsOrder): ProductsOrder {
        return productsOrderJpaRepository.save(productsOrder)
    }

    override fun discountTotalPriceList(buyerId: Long, couponIdList: List<Long>): Int {

        val query = queryFactory.select(
                getTotalPrice().sum()
        ).from(itemCart)
            .leftJoin(couponToBuyer).fetchJoin()
            .on(couponToBuyer.buyer().id.eq(itemCart.buyerId)
                .and(couponToBuyer.coupon().id.`in`(couponIdList)))
            .leftJoin(coupon).fetchJoin()
            .on(coupon.id.eq(couponToBuyer.coupon().id)
                .and(coupon.product().id.eq(itemCart.product().id)))
            .where(itemCart.buyerId.eq(buyerId)
                , itemCart.isDeleted.isFalse)
            .fetchOne()


        return query ?: 0
    }

    private fun getTotalPrice(): NumberExpression<Int>{

        return CaseBuilder()
            .`when`(couponToBuyer.coupon().id.isNotNull
                .and(coupon.discountPolicy.eq(DiscountPolicy.DISCOUNT_RATE)))
            .then(itemCart.price.multiply(itemCart.quantity)
                .subtract(itemCart.price.multiply(itemCart.quantity).multiply(coupon.discount.divide(100.0))))
            .`when`(couponToBuyer.coupon().id.isNotNull
                .and(coupon.discountPolicy.eq(DiscountPolicy.DISCOUNT_PRICE)))
            .then(itemCart.price.multiply(itemCart.quantity)
                .subtract(coupon.discount))
            .otherwise(itemCart.price.multiply(itemCart.quantity))
    }
}


