package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.backoffice.dto.saleshistory.SalesHistoryResponse
import com.highv.ecommerce.domain.backoffice.repository.SalesHistoryRepository
import com.highv.ecommerce.domain.order_status.repository.OrderStatusRepository
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SalesHistoryService(
    private val salesHistoryRepository: SalesHistoryRepository,
    private val productsOrderRepository: ProductsOrderRepository,
    private val orderStatusRepository: OrderStatusRepository
) {
    fun getSalesHistory(sellerId: Long, orderId: Long): SalesHistoryResponse {
        val salesHistory = salesHistoryRepository.findByOrderId(orderId)
        return SalesHistoryResponse.from(salesHistory)
    }

    fun salesStatusChange(
        sellerId: Long,
        orderId: Long,
        orderStatusRequest: OrderStatusRequest
    ): DefaultResponse {
        // seller 검증 구문 필요 ( 추후 refactoring 시 수정 )

        val order = productsOrderRepository.findByIdOrNull(orderId)
            ?: throw RuntimeException("Order not found")

        order.update(orderStatusRequest)
        productsOrderRepository.save(order)

        return DefaultResponse.from("주문 상태 변경 완료. 변경된 상태 : ${orderStatusRequest.statusCode.name}")
    }
}