package com.highv.ecommerce.domain.order_details.repository

import com.highv.ecommerce.domain.item_cart.entity.QItemCart
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class OrderDetailsRepositoryImpl(
    private val orderDetailsJpaRepository: OrderDetailsJpaRepository,
    @PersistenceContext
    private val em: EntityManager
): OrderDetailsRepository {

    private val queryFactory = JPAQueryFactory(em)
//    private val orderStatus = QOrderStatus.orderStatus
//    private val productsOrder = QProductsOrder.productsOrder
    private val cartItem = QItemCart.itemCart

    override fun findAllByShopIdAndOrderMasterId(shopId: Long, productsOrderId: Long): List<OrderDetails> {
        return orderDetailsJpaRepository.findAllByShopIdAndOrderMasterId(shopId, productsOrderId)
    }

    // TODO("수정 필요")
    override fun findAllByBuyerId(id: Long): List<OrderDetails> {

//        val query = queryFactory
//            .selectFrom(orderStatus)
//            .where(
//                orderStatus.buyerId.eq(id),
//                orderStatus.isDeleted.eq(false)
//            )
//            .leftJoin(orderStatus.productsOrder(),productsOrder).fetchJoin()
//            .leftJoin(orderStatus.itemCart(), cartItem).fetchJoin()
//            .fetch()

        return listOf()
    }

    override fun save(orderStatus: OrderDetails): OrderDetails {
        return orderDetailsJpaRepository.save(orderStatus)
    }

    override fun saveAll(orderStatuses: List<OrderDetails>): List<OrderDetails> {
        return orderDetailsJpaRepository.saveAll(orderStatuses)
    }

    // TODO("수정 필요")
    override fun findAllByShopId(shopId: Long): List<OrderDetails> {
//        val query = queryFactory
//            .selectFrom(orderStatus)
//            .where(
//                orderStatus.shopId.eq(shopId),
//                orderStatus.isDeleted.eq(false)
//            )
//            .leftJoin(orderStatus.productsOrder(),productsOrder).fetchJoin()
//            .leftJoin(orderStatus.itemCart(), cartItem).fetchJoin()
//            .fetch()

        return listOf()
    }

    override fun findByItemCartIdAndBuyerId(orderStatusId: Long, buyerId: Long): OrderDetails? {
        TODO()
    }

    override fun findByIdAndShopId(orderStatusId: Long, shopId: Long): OrderDetails? {
        return orderDetailsJpaRepository.findByIdAndBuyerId(shopId, orderStatusId)
    }

    override fun findAllByShopIdAndOrderMasterIdAndBuyerId(shopId: Long, orderId: Long, buyerId: Long): List<OrderDetails> {
        return orderDetailsJpaRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(shopId, orderId, buyerId)
    }
}