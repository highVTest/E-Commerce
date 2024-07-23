package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus

data class OrderStatusResponse(
    val msg: String
){
    companion object{
        fun from(orderStatusType: OrderStatus, str: String): OrderStatusResponse{
            return OrderStatusResponse(
                msg = when(orderStatusType.name){
                    "CANCEL" -> "주문 취소 $str"
                    "EXCHANGE" -> "교환 $str"
                    "REFUND" -> "환불 $str"
                    else -> throw RuntimeException("잘못된 값이 입력 되었습니다")
                }
            )



        }
    }
}