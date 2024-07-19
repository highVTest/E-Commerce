package com.highv.ecommerce.domain.products_order.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import com.highv.ecommerce.domain.order_status.enumClass.RejectReason
import com.highv.ecommerce.domain.order_status.repository.OrderStatusRepository
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProductsOrderService(
    private val productsOrderRepository: ProductsOrderRepository,
    private val orderRejectRepository: OrderStatusRepository,
    private val itemCartRepository: ItemCartRepository,
){

    @Transactional
    fun requestPayment(cartId: Long, userPrincipal: UserPrincipal): DefaultResponse {
        //TODO(카트 아이디 를 조회 해서 물건을 가져 온다 -> List<CartItem>)
        val itemCart = itemCartRepository.findByIdAndBuyerId(cartId, userPrincipal.id) ?: throw RuntimeException("장바구니가 존재 하지 않습 니다")

        //TODO(만약에 CartItem 에 ProductId 가 Coupon 의 ProductId와 일치할 경우 CartItem 의 가격을 임시로 업데이트)

        val productsOrder = productsOrderRepository.saveAndFlush(
            ProductsOrder(
                statusCode = StatusCode.ORDERED,
                buyerId = userPrincipal.id,
                isPaid = false,
                payDate = LocalDateTime.now(),
                totalPrice = itemCart.price,
                deliveryStartAt = LocalDateTime.now(),
                deliveryEndAt = LocalDateTime.now(),
                regDate = LocalDateTime.now(),
            )
        )

        orderRejectRepository.save(
            OrderStatus(
                rejectReason = RejectReason.NONE,
                isBuyer = false,
                isSellerReject = false,
                itemCart = itemCart,
                productsOrder = productsOrder
            )
        )


        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${productsOrder.id}")
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, orderStatusRequest: OrderStatusRequest, userPrincipal: UserPrincipal): DefaultResponse {

        val order = productsOrderRepository.findByIdOrNull(orderId) ?: throw RuntimeException()

        order.update(orderStatusRequest)

        productsOrderRepository.save(order)

        return DefaultResponse.from("주문 상태 변경이 완료 되었습니다. 변경된 상태 : ${orderStatusRequest.statusCode.name}")
    }



}