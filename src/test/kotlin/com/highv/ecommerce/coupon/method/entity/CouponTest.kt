package com.highv.ecommerce.coupon.method.entity

import com.highv.ecommerce.common.exception.CouponExpiredException
import com.highv.ecommerce.common.exception.CouponSoldOutException
import com.highv.ecommerce.common.exception.InvalidDiscountPolicyException
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponTest {



    @Test
    fun `만료 시간이 다르면 CouponExpiredException 처리`() {

        shouldThrow<CouponExpiredException> {
            coupon.validExpiredAt()
        }.let {
            it.message shouldBe "쿠폰 유효 기간이 지났습니다"
        }
    }

    @Test
    fun `spendCoupon 에서 0 이하로 떨어질 경우 CouponSoldOutException 처리`(){
        coupon.quantity = 0

        shouldThrow<CouponSoldOutException> {
            coupon.spendCoupon()
        }.let {
            it.message shouldBe "쿠폰이 매진되었습니다"
        }
    }

    @Test
    fun `update 시에 DiscountPolicy DISCOUNT_RATE 이고 값이 100을 넘을 경우 InvalidDiscountPolicyException 처리`(){

        val updateCouponRequest = UpdateCouponRequest(
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 101,
            quantity = 1,
            expiredAt = LocalDateTime.of(2021, 1, 1, 1, 0),
        )

        shouldThrow<InvalidDiscountPolicyException> {
            coupon.update(updateCouponRequest)
        }.let {
            it.message shouldBe "할인율은 40%를 넘길 수 없습니다"
        }
    }

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
            sellerId = 1L,
        )
    }

}