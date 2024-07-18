package com.highv.ecommerce.domain.products_order.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProductsOrderService(
    private val productsOrderRepository: ProductsOrderRepository
){

    @Transactional
    fun requestPayment(cartId: Long): DefaultResponse {
        //TODO(카트 아이디 를 조회 해서 물건을 가져 온다 -> List<CartItem>)
        //TODO(만약에 CartItem 에 ProductId 가 Coupon 의 ProductId와 일치할 경우 CartItem 의 가격을 임시로 업데이트)

        val result = productsOrderRepository.save(
            ProductsOrder(
                statusCode = StatusCode.ORDERED,
                buyerId = 1L,
                isPaid = false,
                payDate = LocalDateTime.now(),
                totalPrice = 20000,
                deliveryStartAt = LocalDateTime.now(),
                deliveryEndAt = LocalDateTime.now(),
                isCancelled = false,
                cancelDate = null,
                cancelDescription = null,
                isRefund = false,
                refundDate = null,
                refundDescription = null,
                isRefundReject = false,
                refundRejectDate = null,
                refundRejectDescription = null,
                regDate = LocalDateTime.now(),
                deletedAt = null,
                isDeleted = false,
            )
        )
        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${result.id}")
    }
}