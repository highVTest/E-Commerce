package com.highv.ecommerce.coupon.method.entity

import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponTest {

    //Given
    companion object{
        private val shop = Shop(
            sellerId = 1L,
            name = "name",
            description = "description",
            shopImage = "shopImage",
            rate = 10f
        )

        private val product = Product(
            name = "Test product name",
            description = "Test product description",
            productImage = "test",
            favorite = 1,
            createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
        )

        private var coupon = Coupon(
            id = 1L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            deletedAt = null,
            isDeleted = false,
            sellerId = 1L,
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

    @Test
    fun `spendCoupon 에서 0 이하로 떨어질 경우 RuntimeException 처리`(){
        coupon.quantity = 0

        shouldThrow<RuntimeException> {
            coupon.spendCoupon()
        }.let {
            it.message shouldBe "쿠폰이 매진 되었습니다"
        }
    }

    @Test
    fun `update 시에 DiscountPolicy DISCOUNT_RATE 이고 값이 100을 넘을 경우 RuntimeException 처리`(){

        val updateCouponRequest = UpdateCouponRequest(
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 101,
            quantity = 1,
            expiredAt = LocalDateTime.of(2021, 1, 1, 1, 0),
        )

        shouldThrow<RuntimeException> {
            coupon.update(updateCouponRequest)
        }.let {
            it.message shouldBe "할인율은 100%를 넘길 수 없습 니다"
        }
    }

}