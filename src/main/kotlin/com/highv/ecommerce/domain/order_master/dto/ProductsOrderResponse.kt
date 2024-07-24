package com.highv.ecommerce.domain.order_master.dto

import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import java.time.LocalDateTime

data class ProductsOrderResponse(
    val id : Long,
    val statusCode: OrderStatus,
    val complainStatus: ComplainStatus,
    val updatedDate: LocalDateTime,
    val rejectDescription : String? = null,
    val buyerName : String,
    val totalPrice : Int,
){
    // TODO("수정 필요")
    companion object{
        fun from(orderDetails: OrderDetails): ProductsOrderResponse{
            return ProductsOrderResponse(
                id = orderDetails.orderMaster.id!!,
                statusCode = orderDetails.orderStatus,
                complainStatus = orderDetails.complainStatus,
                updatedDate = LocalDateTime.now(),
                rejectDescription = orderDetails.sellerDescription ?: "",
                buyerName = orderDetails.buyer.nickname,
                totalPrice = 1
            )
        }
    }
}