package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class CreateCouponRequest (
    val productId : Long,
    var discountPolicy: DiscountPolicy,

    @field:Min(value = 1, message = "최소 1이어야 합니다")
    @field:Max(value = 50000, message = "최대 50000을 넘길 수 없습니다")
    var discount : Int,

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val expiredAt: LocalDateTime,

    @field:Min(value = 1, message = "최소 1이어야 합니다")
    val quantity : Int,

    @field:Size(min = 4, message = "쿠폰 이름은 최소 4자 이상 이어야 합니다")
    val couponName : String,
)
