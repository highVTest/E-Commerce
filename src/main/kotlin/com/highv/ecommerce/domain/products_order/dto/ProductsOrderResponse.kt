package com.highv.ecommerce.domain.products_order.dto

import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import com.highv.ecommerce.domain.order_status.enumClass.RejectReason
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import java.time.LocalDateTime

data class ProductsOrderResponse(
    val id : Long,
    val statusCode: StatusCode,
    val rejectReason: RejectReason,
    val updatedDate: LocalDateTime,
    val rejectDescription : String? = null,
    val buyerName : String,
    val totalPrice : Int,
){
    companion object{
        fun from(orderReject: OrderStatus): ProductsOrderResponse{
            return ProductsOrderResponse(
                id = orderReject.productsOrder.id!!,
                statusCode = orderReject.productsOrder.statusCode,
                rejectReason = orderReject.rejectReason,
                updatedDate = LocalDateTime.now(),
                rejectDescription = orderReject.sellerDescription ?: "",
                buyerName = orderReject.itemCart.buyerId.toString(),
                totalPrice = orderReject.productsOrder.totalPrice
            )
        }
    }
}