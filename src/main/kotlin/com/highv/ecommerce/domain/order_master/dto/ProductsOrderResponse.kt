package com.highv.ecommerce.domain.order_master.dto

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_master.enumClass.StatusCode
import java.time.LocalDateTime

data class ProductsOrderResponse(
    val id : Long,
    val statusCode: StatusCode,
    val orderPendingReason: ComplainStatus,
    val updatedDate: LocalDateTime,
    val rejectDescription : String? = null,
    val buyerName : String,
    val totalPrice : Int,
){
    companion object{
        fun from(orderReject: OrderDetails): ProductsOrderResponse{
            return ProductsOrderResponse(
                id = orderReject.productsOrder.id!!,
                statusCode = orderReject.productsOrder.statusCode,
                orderPendingReason = orderReject.orderPendingReason,
                updatedDate = LocalDateTime.now(),
                rejectDescription = orderReject.sellerDescription ?: "",
                buyerName = orderReject.itemCart.buyerId.toString(),
                totalPrice = orderReject.productsOrder.totalPrice
            )
        }
    }
}