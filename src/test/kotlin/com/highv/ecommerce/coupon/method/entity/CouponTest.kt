package com.highv.ecommerce.coupon.method.entity

import com.highv.ecommerce.domain.coupon.entity.Coupon
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponTest {

    //Given
    companion object{
        private val coupon = Coupon(
            id = 1L,
            productId = 1,
            discountRate = 50,
            discountPrice = null,
            expiredAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            deletedAt = null,
            isDeleted = false,
        )
    }

    @Test
    fun `만료 시간이 다르면 RuntimeException 처리`() {

        shouldThrow<RuntimeException> {
            coupon.validExpiredAt()
        }.let {
            it.message shouldBe "쿠폰 유호 기간이 지났 습니다"
        }
    }
}