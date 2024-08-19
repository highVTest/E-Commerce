package com.highv.ecommerce.domain.item_cart.repository

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.entity.QItemCart.itemCart
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ItemCartQueryDslImpl(
    private val queryFactory: JPAQueryFactory
) : ItemCartQueryDsl {

    override fun findByBuyerId(buyerId: Long): List<ItemCart> {
        val query = queryFactory
            .select(itemCart)
            .from(itemCart)
            .innerJoin(itemCart.product()).fetchJoin()
            .innerJoin(itemCart.product().productBackOffice()).fetchJoin()
            .innerJoin(itemCart.product().shop()).fetchJoin()
            .innerJoin(itemCart.buyer()).fetchJoin()
            .where(itemCart.buyer().id.eq(buyerId))
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
            .innerJoin(itemCart.buyer()).fetchJoin()
            .where(itemCart.buyer().id.eq(buyerId))
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
            .innerJoin(itemCart.buyer()).fetchJoin()
            .where(itemCart.buyer().id.eq(buyerId))
            .where(itemCart.id.`in`(id))
            .fetch()

        return query
    }

    @Modifying(clearAutomatically = true)
    @Transactional
    override fun deleteAll(cartIdList: List<Long>) {

        queryFactory.delete(itemCart)
            .where(itemCart.id.`in`(cartIdList))
            .execute()
    }
}