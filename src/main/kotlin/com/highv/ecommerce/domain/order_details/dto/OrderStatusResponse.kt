package com.highv.ecommerce.domain.order_details.dto

import com.highv.ecommerce.domain.order_details.enumClass.ComplainType

data class OrderStatusResponse(
    val msg: String
){
    companion object{
        fun from(complainType: ComplainType, str: String): OrderStatusResponse{
            return OrderStatusResponse(
                msg = when(complainType.name){
                    "EXCHANGE" -> "교환 $str"
                    "REFUND" -> "환불 $str"
                    else -> throw RuntimeException("잘못된 값이 입력 되었습니다")
                }
            )



        }
    }
}