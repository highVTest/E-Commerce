package com.highv.ecommerce.domain.order_details.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import java.time.LocalDateTime

data class SellerOrderResponse(
    val orderMasterId: Long,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul", shape = JsonFormat.Shape.STRING)
    val registerDate: LocalDateTime,
    val products: List<SellerComplainResponse>
) {
    companion object {
        fun from(orderMaster: OrderMaster, orderDetails: List<OrderDetails>): SellerOrderResponse {
            return SellerOrderResponse(
                orderMasterId = orderMaster.id!!,
                registerDate = orderMaster.regDateTime,
                products = orderDetails.map { SellerComplainResponse.from(it) }
            )
        }
    }
}