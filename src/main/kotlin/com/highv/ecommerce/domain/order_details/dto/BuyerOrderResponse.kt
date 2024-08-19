package com.highv.ecommerce.domain.order_details.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import java.time.LocalDateTime

data class BuyerOrderResponse(
    val orderMasterId: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    val orderRegisterDate: LocalDateTime,
    val orderShopDetails: List<BuyerOrderShopResponse>
) {
    companion object {
        fun from(productsOrder: OrderMaster, orderShopDetails: List<BuyerOrderShopResponse>): BuyerOrderResponse =
            BuyerOrderResponse(
                orderMasterId = productsOrder.id!!,
                orderRegisterDate = productsOrder.regDateTime,
                orderShopDetails = orderShopDetails
            )
    }
}
