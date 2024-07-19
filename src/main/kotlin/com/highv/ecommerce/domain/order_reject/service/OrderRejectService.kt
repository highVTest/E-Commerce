package com.highv.ecommerce.domain.order_reject.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_reject.enumClass.RejectReason
import com.highv.ecommerce.domain.order_reject.repository.OrderRejectRepository
import com.highv.ecommerce.domain.products_order.dto.DescriptionRequest
import com.highv.ecommerce.domain.products_order.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderRejectService(
    private val orderRejectRepository: OrderRejectRepository,
    private val productsOrderRepository: ProductsOrderRepository,
    private val itemCartRepository: ItemCartRepository
){

    @Transactional
    fun requestRefund(orderRejectId: Long, descriptionRequest: DescriptionRequest, userPrincipal: UserPrincipal): DefaultResponse {

        val orderReject = orderRejectRepository.findByIdOrNull(orderRejectId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderReject.productsOrder.update(StatusCode.PENDING)

        orderReject.buyerUpdate(RejectReason.REFUND_REQUESTED, descriptionRequest)

        orderRejectRepository.save(orderReject)

        return DefaultResponse.from("환불 요청 완료 되었습니다")
    }

    @Transactional
    fun requestRefundReject(orderRejectId: Long, descriptionRequest: DescriptionRequest, userPrincipal: UserPrincipal): DefaultResponse {

        val orderReject = orderRejectRepository.findByIdOrNull(orderRejectId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        orderReject.productsOrder.update(StatusCode.PENDING)

        orderReject.sellerUpdate(RejectReason.REFUND_REJECTED, descriptionRequest)

        orderRejectRepository.save(orderReject)

        return DefaultResponse.from("환불 거절 요청 완료 되었습니다")
    }

    fun getOrderDetails(orderRejectId: Long, userPrincipal: UserPrincipal): ProductsOrderResponse {
        val orderReject = orderRejectRepository.findByIdOrNull(orderRejectId) ?: throw RuntimeException("주문 정보가 존재 하지 않습니다")

        return ProductsOrderResponse.from(orderReject)
    }

    @Transactional
    fun requestOrderCanceled(orderId: Long, userPrincipal: UserPrincipal): DefaultResponse {
        return DefaultResponse.from("주문 취소가 완료 되었습니다")
    }
}