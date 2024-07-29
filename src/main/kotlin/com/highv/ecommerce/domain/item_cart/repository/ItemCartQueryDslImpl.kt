package com.highv.ecommerce.domain.item_cart.repository

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.entity.QItemCart.itemCart
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ItemCartQueryDslImpl(
    private val queryFactory: JPAQueryFactory
) : ItemCartQueryDsl {

    // private val product = QProduct.product
    // private val itemCart = QItemCart.itemCart

    override fun findByBuyerId(buyerId: Long): List<ItemCart> {
        val query = queryFactory
            .select(itemCart)
            .from(itemCart)
            .innerJoin(itemCart.product()).fetchJoin()
            .innerJoin(itemCart.product().productBackOffice()).fetchJoin()
            .innerJoin(itemCart.product().shop()).fetchJoin()
            .where(itemCart.buyerId.eq(buyerId))
            .fetch()

        return query
    }

    override fun findByProductIdAndBuyerId(productId: Long, buyerId: Long): ItemCart? {
        val query = queryFactory
            .select(itemCart)
            .from(itemCart)
            .innerJoin(itemCart.product()).fetchJoin()
            .innerJoin(itemCart.product().productBackOffice()).fetchJoin()
            .innerJoin(itemCart.product().shop()).fetchJoin()
            .where(itemCart.buyerId.eq(buyerId))
            .where(itemCart.product().id.eq(productId))
            .fetchOne()

        return query
    }

    override fun findAllByIdAndBuyerId(id: List<Long>, buyerId: Long): List<ItemCart> {
        val query = queryFactory
            .select(itemCart)
            .from(itemCart)
            .innerJoin(itemCart.product()).fetchJoin()
            .innerJoin(itemCart.product().productBackOffice()).fetchJoin()
            .innerJoin(itemCart.product().shop()).fetchJoin()
            .where(itemCart.buyerId.eq(buyerId))
            .where(itemCart.id.`in`(id))
            .fetch()

        return query
    }
}