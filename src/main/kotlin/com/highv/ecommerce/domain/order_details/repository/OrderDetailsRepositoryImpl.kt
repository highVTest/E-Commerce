package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.entity.QOrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
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
    }

    override fun save(orderStatus: OrderDetails): OrderDetails {
        return orderDetailsJpaRepository.save(orderStatus)
    }

    override fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails> {
        return orderDetailsJpaRepository.saveAll(orderStatuses)
    }

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

    override fun updateDeliveryStatus(changeStatus: OrderStatus, whereStatus: OrderStatus) {
        orderDetailsJpaRepository.updateDeliveryStatus(changeStatus, whereStatus)
    }

    override fun totalSalesStatisticsByShop(shopId: Long): TotalSalesResponse {
        val result = queryFactory
            .select(
                orderDetails.productQuantity.sum(),
                orderDetails.totalPrice.sum()
            )
            .from(orderDetails)
            .where(orderDetails.shop().id.eq(shopId).and(orderDetails.orderStatus.ne(OrderStatus.ORDER_CANCELED)))
            .fetchOne()

        val totalSalesQuantity = result?.get(0, Int::class.java)?.toInt() ?: 0
        val totalSalesAmount = result?.get(1, Int::class.java)?.toInt() ?: 0

        return TotalSalesResponse(totalSalesQuantity, totalSalesAmount)
    }

    override fun findAllByShopIdOrderStatus(shopId: Long, orderStatus: OrderStatus): List<OrderDetails> {
        val query = queryFactory
            .selectFrom(orderDetails)
            .innerJoin(orderDetails.product()).fetchJoin()
            .innerJoin(orderDetails.product().shop()).fetchJoin()
            .innerJoin(orderDetails.buyer()).fetchJoin()
            .innerJoin(orderDetails.product().productBackOffice()).fetchJoin()
            .where(orderDetails.product().shop().id.eq(shopId))
            .where(orderDetails.orderStatus.eq(orderStatus))
            .fetch()

        return query
    }
}