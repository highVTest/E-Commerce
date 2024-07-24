package com.highv.ecommerce.domain.backoffice.dto.saleshistory

import java.time.LocalDateTime

data class SalesHistoryResponse(
    val id: Long,
    val sellerId: Long,
    val price: Int,
    val regDt: LocalDateTime,
    val buyerName: String,
    val orderId: Long,
) {
    // TODO("수정 및 삭제 필요")
//    companion object {
//        fun from(salesHistory: SalesHistory) = SalesHistoryResponse(
//            salesHistory.id!!,
//            salesHistory.sellerId,
//            salesHistory.price,
//            salesHistory.regDt,
//            salesHistory.buyerName,
//            salesHistory.orderId,
//        )
//    }
}