package com.highv.ecommerce.domain.order_details.service

import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.repository.OrderStatusRepository
import com.highv.ecommerce.domain.order_master.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderDetailsService(
    private val orderStatusRepository: OrderStatusRepository,
){


    @Transactional
    fun requestOrderStatusChange(itemCartId: Long, buyerOrderStatusRequest: BuyerOrderStatusRequest, buyerId: Long): OrderStatusResponse {

        val orderDetails = orderStatusRepository.findByItemCartIdAndBuyerId(itemCartId, buyerId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderDetails.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)

        orderStatusRepository.save(orderDetails)

        return OrderStatusResponse.from(buyerOrderStatusRequest.complainType,"요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusReject(orderStatusId: Long, shopId: Long, sellerOrderStatusRequest: SellerOrderStatusRequest, sellerId: Long): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findByIdAndShopId(orderStatusId, shopId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderStatus.sellerUpdate(OrderStatus.ORDERED,sellerOrderStatusRequest)

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

        val orderDetails = orderStatusRepository.findAllByShopIdAndProductsOrderId(buyerOrderStatusRequest.shopId, buyerId)

        orderDetails.map {
            it.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
        }

        orderStatusRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(buyerOrderStatusRequest.complainType ,"전체 요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusRejectList(sellerOrderStatusRequest: SellerOrderStatusRequest, sellerId: Long): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findAllByShopIdAndProductsOrderId(sellerOrderStatusRequest.shopId, sellerOrderStatusRequest.buyerId)

        orderStatus.map {
            it.sellerUpdate(OrderStatus.ORDERED, sellerOrderStatusRequest)
        }

        orderStatusRepository.saveAll(orderStatus)

        return OrderStatusResponse.from(sellerOrderStatusRequest.orderStatusType,"전체 요청 거절 완료 되었습니다")


    }


}