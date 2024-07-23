package com.highv.ecommerce.domain.order_status.service

import com.highv.ecommerce.domain.order_status.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_status.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_status.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_status.repository.OrderStatusRepository
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderStatusService(
    private val orderStatusRepository: OrderStatusRepository,
){


    @Transactional
    fun requestOrderStatusChange(itemCartId: Long, buyerOrderStatusRequest: BuyerOrderStatusRequest, buyerId: Long): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findByItemCartIdAndBuyerId(itemCartId, buyerId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderStatus.productsOrder.update(StatusCode.PENDING)

        orderStatus.buyerUpdate(buyerOrderStatusRequest)

        orderStatusRepository.save(orderStatus)

        return OrderStatusResponse.from(buyerOrderStatusRequest.orderStatusType,"요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusReject(orderStatusId: Long, shopId: Long, sellerOrderStatusRequest: SellerOrderStatusRequest, sellerId: Long): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findByIdAndShopId(orderStatusId, shopId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderStatus.productsOrder.update(StatusCode.ORDERED)

        orderStatus.sellerUpdate(sellerOrderStatusRequest)

        orderStatusRepository.save(orderStatus)

        return OrderStatusResponse.from(sellerOrderStatusRequest.orderStatusType ,"요청 거절 완료 되었습니다")
    }

    fun getBuyerOrderDetails(buyerId: Long): List<ProductsOrderResponse> {

        val orderStatus = orderStatusRepository.findAllByBuyerId(buyerId)

        return orderStatus.map { ProductsOrderResponse.from(it) }
    }

    fun getSellerOrderDetails(shopId: Long , sellerId: Long): List<ProductsOrderResponse> {

        val orderStatus = orderStatusRepository.findAllByShopId(shopId)

        return orderStatus.map { ProductsOrderResponse.from(it) }
    }

    @Transactional
    fun requestOrderStatusChangeList(buyerOrderStatusRequest: BuyerOrderStatusRequest, buyerId: Long): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findAllByShopIdAndProductsOrderId(buyerOrderStatusRequest.shopId, buyerId)

        orderStatus.map {
            it.productsOrder.statusCode = StatusCode.PENDING
            it.buyerUpdate(buyerOrderStatusRequest)
        }

        orderStatusRepository.saveAll(orderStatus)

        return OrderStatusResponse.from(buyerOrderStatusRequest.orderStatusType,"전체 요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusRejectList(sellerOrderStatusRequest: SellerOrderStatusRequest, sellerId: Long): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findAllByShopIdAndProductsOrderId(sellerOrderStatusRequest.shopId, sellerOrderStatusRequest.buyerId)

        orderStatus.map {
            it.productsOrder.statusCode = StatusCode.ORDERED
            it.sellerUpdate(sellerOrderStatusRequest)
        }

        orderStatusRepository.saveAll(orderStatus)

        return OrderStatusResponse.from(sellerOrderStatusRequest.orderStatusType,"전체 요청 거절 완료 되었습니다")


    }


}