package com.highv.ecommerce.domain.order_status.service

import com.highv.ecommerce.domain.order_status.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_status.dto.OrderListRequest
import com.highv.ecommerce.domain.order_status.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_status.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_status.repository.OrderStatusRepository
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderStatusService(
    private val orderStatusRepository: OrderStatusRepository,
){


    @Transactional
    fun requestOrderStatusChange(orderStatusId: Long, descriptionRequest: DescriptionRequest, userPrincipal: UserPrincipal): OrderStatusResponse {

        val orderReject = orderStatusRepository.findByIdOrNull(orderStatusId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderReject.productsOrder.update(StatusCode.PENDING)

        orderReject.buyerUpdate(descriptionRequest.orderStatusType, descriptionRequest)

        orderStatusRepository.save(orderReject)

        return OrderStatusResponse.from(descriptionRequest.orderStatusType,"요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusReject(orderStatusId: Long, descriptionRequest: DescriptionRequest, userPrincipal: UserPrincipal): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findByIdOrNull(orderStatusId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderStatus.productsOrder.update(StatusCode.PENDING)

        orderStatus.sellerUpdate(descriptionRequest.orderStatusType, descriptionRequest)

        orderStatusRepository.save(orderStatus)

        return OrderStatusResponse.from(descriptionRequest.orderStatusType ,"요청 거절 완료 되었습니다")
    }

    fun getOrderDetails(orderStatusId: Long, userPrincipal: UserPrincipal): ProductsOrderResponse {

        val orderStatus = orderStatusRepository.findByIdOrNull(orderStatusId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        return ProductsOrderResponse.from(orderStatus)
    }

    @Transactional
    fun requestOrderStatusChangeList(buyerOrderStatusRequest: BuyerOrderStatusRequest, userPrincipal: UserPrincipal): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findAllByShopIdAndBuyerId(buyerOrderStatusRequest.shopId, userPrincipal.id)

        orderStatus.map {
            it.buyerUpdate(buyerOrderStatusRequest.orderStatusType, buyerOrderStatusRequest.description)
        }

        orderStatusRepository.saveAll(orderStatus)

        return OrderStatusResponse.from(buyerOrderStatusRequest.orderStatusType,"전체 요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusRejectList(sellerOrderStatusRequest: SellerOrderStatusRequest, userPrincipal: UserPrincipal): OrderStatusResponse {

        val orderStatus = orderStatusRepository.findAllByShopIdAndBuyerId(sellerOrderStatusRequest.shopId, sellerOrderStatusRequest.shopId)

        orderStatus.map {
            it.sellerUpdate(sellerOrderStatusRequest.orderStatusType, sellerOrderStatusRequest.description)
        }

        orderStatusRepository.saveAll(orderStatus)

        return OrderStatusResponse.from(sellerOrderStatusRequest.orderStatusType,"전체 요청 거절 완료 되었습니다")


    }


}