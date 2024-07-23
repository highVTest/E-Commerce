package com.highv.ecommerce.domain.buyer.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import java.time.LocalDateTime

data class BuyerOrderResponse(
    val productsOrderId: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    val orderRegisterDate: LocalDateTime,
    val orderStatus: StatusCode,
    val productsOrders: List<BuyerHistoryProductResponse>
) {
    companion object {
        fun from(productsOrder: ProductsOrder, products: List<BuyerHistoryProductResponse>): BuyerOrderResponse =
            BuyerOrderResponse(
                productsOrderId = productsOrder.id!!,
                orderRegisterDate = productsOrder.regDate,
                orderStatus = productsOrder.statusCode,
                productsOrders = products
            )
    }
}
