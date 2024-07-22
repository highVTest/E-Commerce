package com.highv.ecommerce.coupon.method.dto

import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import kotlin.test.Test

class CreateCouponRequestTest {

    @Test
    fun `toLocalDateTime이 LocalDateTime 타입 양식으로 바뀌 는지 확인`(){
        val expected = LocalDateTime.of(2024, 8, 1, 0, 0)

        val createCouponRequest = CreateCouponRequest(
            productId = 1L,
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = expected,
            quantity = 0
        )


        val result = createCouponRequest
        result shouldBe expected
    }
}