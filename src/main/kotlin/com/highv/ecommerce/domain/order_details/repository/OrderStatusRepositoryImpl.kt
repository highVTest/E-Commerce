package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.entity.QOrderStatus
import com.highv.ecommerce.domain.order_master.entity.QProductsOrder
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class OrderStatusRepositoryImpl(
    private val orderStatusJpaRepository: OrderStatusJpaRepository,
    @PersistenceContext
    private val em: EntityManager
): OrderStatusRepository {

    private val queryFactory = JPAQueryFactory(em)
    private val orderStatus = QOrderStatus.orderStatus
    private val productsOrder = QProductsOrder.productsOrder
    private val cartItem = QItemCart.itemCart

    override fun findAllByShopIdAndProductsOrderId(shopId: Long, productsOrderId: Long): List<OrderDetails> {
        return orderStatusJpaRepository.findAllByShopIdAndProductsOrderIdAndIsDeletedFalse(shopId, productsOrderId)
    }

    override fun findAllByBuyerId(id: Long): List<OrderDetails> {

        val query = queryFactory
            .selectFrom(orderStatus)
            .where(
                orderStatus.buyerId.eq(id),
                orderStatus.isDeleted.eq(false)
            )
            .leftJoin(orderStatus.productsOrder(),productsOrder).fetchJoin()
            .leftJoin(orderStatus.itemCart(), cartItem).fetchJoin()
            .fetch()

        return query
    }

    override fun save(orderStatus: OrderDetails): OrderDetails {
        return orderStatusJpaRepository.save(orderStatus)
    }

    override fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails> {
        return orderStatusJpaRepository.saveAll(orderStatuses)
    }

    override fun findAllByShopId(shopId: Long): List<OrderDetails> {
        val query = queryFactory
            .selectFrom(orderStatus)
            .where(
                orderStatus.shopId.eq(shopId),
                orderStatus.isDeleted.eq(false)
            )
            .leftJoin(orderStatus.productsOrder(),productsOrder).fetchJoin()
            .leftJoin(orderStatus.itemCart(), cartItem).fetchJoin()
            .fetch()

        return query
    }

    override fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderDetails? {
        return orderStatusJpaRepository.findByItemCartIdAndBuyerIdAndIsDeletedFalse(orderStatusId, buyerId)
    }

    override fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderDetails? {
        return orderStatusJpaRepository.findByIdAndShopIdAndIsDeletedFalse(shopId, orderStatusId)
    }
}