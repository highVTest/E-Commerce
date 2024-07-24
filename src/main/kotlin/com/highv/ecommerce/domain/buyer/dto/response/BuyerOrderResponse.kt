package com.highv.ecommerce.domain.buyer.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import java.time.LocalDateTime

data class BuyerOrderResponse(
    val productsOrderId: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    val orderRegisterDate: LocalDateTime,
    val orderStatus: OrderStatus,
//    val productsOrders: List<BuyerHistoryProductResponse> // 수정 필요
) {
    companion object {
        fun from(productsOrder: OrderMaster, products: List<BuyerHistoryProductResponse>): BuyerOrderResponse =
            BuyerOrderResponse(
                productsOrderId = productsOrder.id!!,
                orderRegisterDate = productsOrder.regDateTime,
                orderStatus = OrderStatus.ORDERED, // 수정 필요
//                productsOrders = products // 수정 필요
            )
    }
}
