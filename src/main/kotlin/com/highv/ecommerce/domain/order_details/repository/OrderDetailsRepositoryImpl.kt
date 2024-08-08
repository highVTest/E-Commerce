package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.entity.QOrderDetails
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class OrderDetailsRepositoryImpl(
    private val orderDetailsJpaRepository: OrderDetailsJpaRepository,
    @PersistenceContext
    private val em: EntityManager
) : OrderDetailsRepository {

    private val queryFactory = JPAQueryFactory(em)

    // private val orderStatus = QOrderStatus.orderStatus
    // private val productsOrder = QProductsOrder.productsOrder
    private val cartItem = QItemCart.itemCart
    private val orderDetails = QOrderDetails.orderDetails

    override fun findAllByBuyerIdAndOrderMasterId(buyerId: Long, orderId: Long): List<OrderDetails> {
        val query = queryFactory
            .select(orderDetails)
            .from(orderDetails)
            .innerJoin(orderDetails.product()).fetchJoin()
            .innerJoin(orderDetails.product().productBackOffice()).fetchJoin()
            .innerJoin(orderDetails.product().shop()).fetchJoin()
            .innerJoin(orderDetails.buyer()).fetchJoin()
            .where(orderDetails.buyer().id.eq(buyerId))
            .where(orderDetails.orderMasterId.eq(orderId))
            .fetch()

        return query
    }

    override fun findAllByBuyerId(buyerId: Long): List<OrderDetails> {
        // 연관관계 N+1 문제 때문에 아래와 같이 fetchJoin 사용
        val query = queryFactory
            .select(orderDetails)
            .from(orderDetails)
            .innerJoin(orderDetails.product()).fetchJoin()
            .innerJoin(orderDetails.product().productBackOffice()).fetchJoin()
            .innerJoin(orderDetails.product().shop()).fetchJoin()
            .innerJoin(orderDetails.buyer()).fetchJoin()
            .where(orderDetails.buyer().id.eq(buyerId))
            .fetch()

        return query
        // return orderDetailsJpaRepository.findAllByBuyerId(buyerId)
    }

    override fun save(orderStatus: OrderDetails): OrderDetails {
        return orderDetailsJpaRepository.save(orderStatus)
    }

    override fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails> {
        return orderDetailsJpaRepository.saveAll(orderStatuses)
    }

    // TODO("수정 필요")
    override fun findAllByShopId(shopId: Long): List<OrderDetails> {
        val query = queryFactory
            .selectFrom(orderDetails)
            .innerJoin(orderDetails.product()).fetchJoin()
            .innerJoin(orderDetails.product().shop()).fetchJoin()
            .innerJoin(orderDetails.buyer()).fetchJoin()
            .innerJoin(orderDetails.product().productBackOffice()).fetchJoin()
            .where(orderDetails.product().shop().id.eq(shopId))
            .fetch()

        return query
    }

    override fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderDetails? {
        TODO()
    }

    override fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderDetails? {
        return orderDetailsJpaRepository.findByIdAndBuyerId(shopId, orderStatusId)
    }

    override fun findAllByShopIdAndOrderMasterId(shopId: Long, orderMasterId: Long): List<OrderDetails> {
        val query = queryFactory
            .select(orderDetails)
            .from(orderDetails)
            .innerJoin(orderDetails.product()).fetchJoin()
            .innerJoin(orderDetails.product().productBackOffice()).fetchJoin()
            .innerJoin(orderDetails.product().shop()).fetchJoin()
            .innerJoin(orderDetails.buyer()).fetchJoin()
            .where(orderDetails.orderMasterId.eq(orderMasterId))
            .where(orderDetails.shop().id.eq(shopId))
            .fetch()

        return query
    }

    override fun findAllByShopIdAndBuyerId(shopId: Long, buyerId: Long): List<OrderDetails> {
        return orderDetailsJpaRepository.findAllByShopIdAndBuyerId(shopId, buyerId)
    }
}