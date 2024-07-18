package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class CreateCouponRequest (
    val productId : Long,
    val discountPolicy: DiscountPolicy,

    @field:Min(value = 1, message = "최소 1이어야 합니다")
    @field:Max(value = 50000, message = "최대 50000을 넘길 수 없습니다")
    val discount : Int,
    val expiredAt: LocalDateTime,
    val quantity : Int
){
//    fun toLocalDateTime(): LocalDateTime {
//        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
//        val localDate = LocalDateTime.parse(expiredAt, formatter)
//        return localDate.atZone(ZoneId.of("UTC")).toLocalDateTime()
//    }
}
