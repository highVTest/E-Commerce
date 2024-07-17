package com.highv.ecommerce.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.service.CouponService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test

class CouponServiceTest {

    private val couponRepository = mockk<CouponRepository>()
    private val couponService = CouponService(couponRepository)

    @Test
    fun `쿠폰이 정상적으로 등록이 되는 지 확인`(){

        every { couponRepository.save(any()) } returns coupon

        val result = couponService.createCoupon(createCouponRequest)

        result shouldBe defaultResponse("쿠폰 생성이 완료 되었습니다")
    }



    companion object{
        val createCouponRequest = CreateCouponRequest(
            productId = 1,
            discountRate = null,
            discountPrice = null,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1
        )

        val coupon = Coupon(
            id = 1L,
            productId = 1,
            discountRate = null,
            discountPrice = null,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            deletedAt = null,
            isDeleted = false,
        )

        fun defaultResponse(msg: String) = DefaultResponse(msg)
    }
}