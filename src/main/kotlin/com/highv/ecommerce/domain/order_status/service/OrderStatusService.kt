package com.highv.ecommerce.domain.order_status.service

import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_status.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_status.enumClass.RejectReason
import com.highv.ecommerce.domain.order_status.repository.OrderStatusRepository
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderStatusService(
    private val orderStatusRepository: OrderStatusRepository,
    private val productsOrderRepository: ProductsOrderRepository,
    private val itemCartRepository: ItemCartRepository
){


    @Transactional
    fun requestOrderStatusChange(orderRejectId: Long, descriptionRequest: DescriptionRequest, userPrincipal: UserPrincipal): OrderStatusResponse {

        val orderReject = orderStatusRepository.findByIdOrNull(orderRejectId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderReject.productsOrder.update(StatusCode.PENDING)

        orderReject.buyerUpdate(RejectReason.REFUND_REQUESTED, descriptionRequest)

        orderStatusRepository.save(orderReject)

        return OrderStatusResponse.from(descriptionRequest.orderStatusType,"요청 완료 되었습니다")
    }

    @Transactional
    fun requestOrderStatusReject(orderRejectId: Long, descriptionRequest: DescriptionRequest, userPrincipal: UserPrincipal): OrderStatusResponse {

        val orderReject = orderStatusRepository.findByIdOrNull(orderRejectId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderReject.productsOrder.update(StatusCode.PENDING)

        orderReject.sellerUpdate(RejectReason.REFUND_REJECTED, descriptionRequest)

        orderStatusRepository.save(orderReject)

        return OrderStatusResponse.from(descriptionRequest.orderStatusType ,"요청 거절 완료 되었습니다")
    }

    fun getOrderDetails(orderRejectId: Long, userPrincipal: UserPrincipal): ProductsOrderResponse {

        val orderReject = orderStatusRepository.findByIdOrNull(orderRejectId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        return ProductsOrderResponse.from(orderReject)
    }


}