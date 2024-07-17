package com.highv.ecommerce.domain.coupon.dto

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class CreateCouponRequest (
    val productId : Long,
    val discountRate : Int?,
    val discountPrice : Int?,
    val expiredAt: LocalDateTime,
    val quantity : Int
){
//    fun toLocalDateTime(): LocalDateTime {
//        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
//        val localDate = LocalDateTime.parse(expiredAt, formatter)
//        return localDate.atZone(ZoneId.of("UTC")).toLocalDateTime()
//    }
}
