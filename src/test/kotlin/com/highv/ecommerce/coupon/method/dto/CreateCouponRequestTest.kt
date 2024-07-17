package com.highv.ecommerce.coupon.method.dto

import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import kotlin.test.Test

class CreateCouponRequestTest {

    @Test
    fun `toLocalDateTime이 LocalDateTime 타입 양식으로 바뀌 는지 확인`(){

        val createCouponRequest = CreateCouponRequest(
            productId = 1L,
            discountRate = null,
            discountPrice = null,
            expiredAt = "20240801",
            quantity = 0
        )


        val result = createCouponRequest.toLocalDateTime()
        val expected = LocalDateTime.of(2024, 8, 1, 0, 0)
        result shouldBe expected
    }
}