package com.highv.ecommerce.domain.order_status.repository

import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import com.highv.ecommerce.domain.order_status.entity.QOrderStatus
import com.highv.ecommerce.domain.products_order.entity.QProductsOrder
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.findByIdOrNull
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

    override fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderStatus> {
        return orderStatusJpaRepository.findAllByShopIdAndBuyerId(shopId, buyerId)
    }

    override fun findAllByBuyerId(id: Long): List<OrderStatus> {

        val query = queryFactory
            .selectFrom(orderStatus)
            .where(orderStatus.buyerId.eq(id))
            .leftJoin(orderStatus.productsOrder(),productsOrder).fetchJoin()
            .leftJoin(orderStatus.itemCart(), cartItem).fetchJoin()
            .fetch()

        return query
    }

    override fun findByIdOrNull(id: Long): OrderStatus? {
        return orderStatusJpaRepository.findByIdOrNull(id)
    }

    override fun save(orderStatus: OrderStatus): OrderStatus {
        return orderStatusJpaRepository.save(orderStatus)
    }

    override fun saveAll(orderStatuses: List<OrderStatus>): List<OrderStatus> {
        return orderStatusJpaRepository.saveAll(orderStatuses)
    }

    override fun findAllByShopId(shopId: Long): List<OrderStatus> {
        val query = queryFactory
            .selectFrom(orderStatus)
            .where(orderStatus.shopId.eq(shopId))
            .leftJoin(orderStatus.productsOrder(),productsOrder).fetchJoin()
            .leftJoin(orderStatus.itemCart(), cartItem).fetchJoin()
            .fetch()

        return query
    }
}