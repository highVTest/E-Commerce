package com.highv.ecommerce.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.coupon.service.CouponService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import kotlin.test.Test

class CouponServiceTest {

    private val couponRepository = mockk<CouponRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val couponToBuyerRepository = mockk<CouponToBuyerRepository>()
    private val buyerRepository = mockk<BuyerRepository>()
    private val couponService = CouponService(couponRepository, productRepository, couponToBuyerRepository, buyerRepository)
    private val userPrincipal = mockk<UserPrincipal>()

    //Given
    companion object{

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
            shopId = 1L,
            categoryId = 1L,
        )
        private var createCouponRequest = CreateCouponRequest(
            productId = 1,
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1
        )

        private val updateCouponRequest = UpdateCouponRequest(
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1
        )

        private val coupon = Coupon(
            id = 1L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            deletedAt = null,
            isDeleted = false,
            sellerId = 1L
        )

        private val coupon2 = Coupon(
            id = 2L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 50,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            deletedAt = null,
            isDeleted = false,
            sellerId = 1L
        )

        private val coupon3 = Coupon(
            id = 3L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 50,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            deletedAt = null,
            isDeleted = false,
            sellerId = 2L
        )

        private val buyer1 = Buyer(
            id = 1L,
            nickname = "eeee",
            password = "test",
            email = "eeeeee@eee.com",
            profileImage = "test",
            phoneNumber = "1234567890",
            address = "address",
            providerName = null,
            providerId = null,
        )

        val couponToBuyer = CouponToBuyer(
            id = 1L,
            coupon = coupon,
            buyer = buyer1,
        )


        fun defaultResponse(msg: String) = DefaultResponse(msg)
    }

    @Test
    fun `쿠폰의 정책이 DISCOUNT_RATE 이고 CreateCouponRequest 의 값이 100 이상일 경우 RuntimeException을 벌생`() {

        createCouponRequest.discountPolicy = DiscountPolicy.DISCOUNT_RATE
        createCouponRequest.discount = 101

        shouldThrow<RuntimeException> {
            couponService.createCoupon(createCouponRequest, userPrincipal)
        }.let {
            it.message shouldBe "할인율은 100%를 넘길 수 없습 니다"
        }

    }




    @Test
    fun `쿠폰이 정상적으로 등록이 되는 지 확인`(){

        every { userPrincipal.id } returns 1
        every { productRepository.findByIdOrNull(any()) } returns product
        every { couponRepository.existsByProductId(any()) } returns false
        every { couponRepository.save(any()) } returns coupon

        val result = couponService.createCoupon(createCouponRequest, userPrincipal)

        result shouldBe defaultResponse("쿠폰 생성이 완료 되었습니다")
    }

    @Test
    fun `쿠폰이 정상적으로 업데이트가 되는지 확인`(){

        every { couponRepository.findByIdOrNull(any()) } returns coupon

        val result = couponService.updateCoupon(1L, updateCouponRequest, userPrincipal)

        result shouldBe defaultResponse("쿠폰 업데이트가 완료 되었습니다")
    }

    @Test
    fun `쿠폰이 정상적으로 삭제가 되는지 확인`(){

        every { couponRepository.findByIdOrNull(any()) } returns coupon
        every { couponRepository.delete(any()) } returns Unit

        val result = couponService.deleteCoupon(1L, userPrincipal)

        result shouldBe defaultResponse("쿠폰 삭제가 완료 되었습니다")
    }

    @Test
    fun `판메자가 쿠폰을 단건 조회 할 수 있게 확인`(){

        coupon.product.id = 1L
        every { userPrincipal.id } returns 1
        every { couponRepository.findByIdAndSellerId(any(), any()) } returns coupon

        val result = couponService.getSellerCouponById(1L, userPrincipal)


        result.productId shouldBe 1L
        result.expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
        result.discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result.discount shouldBe 30000
    }

    @Test
    fun `판메자가 쿠폰을 여러건 조회 할 수 있게 확인`(){

        coupon.product.id = 1L
        coupon2.product.id = 1L

        every { userPrincipal.id } returns 1
        every { couponRepository.findAllBySellerId(any()) } returns listOf(coupon, coupon2)

        val result = couponService.getSellerCouponList(userPrincipal)


        result.size shouldBe 2

        result[0].productId shouldBe 1L
        result[0].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
        result[0].discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result[0].discount shouldBe 30000

        result[1].productId shouldBe 1L
        result[1].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
        result[1].discountPolicy shouldBe DiscountPolicy.DISCOUNT_RATE.name
        result[1].discount shouldBe 50
    }


    @Test
    fun `구메자가 쿠폰을 단건 조회 할 수 있게 확인`(){
        every { couponRepository.findByIdOrNull(any()) } returns coupon

        val result = couponService.getSellerCouponById(1L, userPrincipal)


        result.productId shouldBe 1L
        result.expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
        result.discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE
        result.discount shouldBe 30000
    }


    @Test
    fun `구메자가 쿠폰을 여러건 조회 할 수 있게 확인`(){

        coupon.product.id = 1L
        coupon2.product.id = 1L

        every { userPrincipal.id } returns 1
        every { couponToBuyerRepository.findAllProductIdWithBuyerId(any()) } returns listOf(1L, 2L)
        every { couponRepository.findAllCouponIdWithBuyer(any()) } returns listOf(coupon, coupon2)
        every { couponRepository.findAllBySellerId(any()) } returns listOf(coupon, coupon2)
        val result = couponService.getSellerCouponList(userPrincipal)


        result[0].productId shouldBe 1L
        result[0].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
        result[0].discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result[0].discount shouldBe 30000

        result[1].productId shouldBe 1L
        result[1].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
        result[1].discountPolicy shouldBe DiscountPolicy.DISCOUNT_RATE.name
        result[1].discount shouldBe 50
    }

    @Test
    fun `쿠폰이 발급될 경우에 동일한 쿠폰은 지급이 안되 는지 확인`(){

        every { userPrincipal.email } returns "eeeeee@eee.com"

        every { buyerRepository.findByEmail(any()) } returns buyer1

        every { couponRepository.findByIdOrNull(any()) } returns coupon

        every { couponToBuyerRepository.existsByCouponIdAndBuyerId(any(), any()) } returns true

        shouldThrow<RuntimeException> {
            couponService.issuedCoupon(1L, userPrincipal)
        }.let {
            it.message shouldBe "동일한 쿠폰은 지급 받을 수 없습니다"
        }
    }

    @Test
    fun `쿠폰이 발급될 경우에 정상적 으로 발급이 되는지 확인`(){

        every { userPrincipal.email } returns "eeeeee@eee.com"

        every { buyerRepository.findByEmail(any()) } returns buyer1

        every { couponRepository.findByIdOrNull(any()) } returns coupon

        every { couponToBuyerRepository.existsByCouponIdAndBuyerId(any(), any()) } returns false

        every { couponToBuyerRepository.save(any()) } returns couponToBuyer

        every { couponRepository.save(any()) } returns coupon

        val result = couponService.issuedCoupon(1L, userPrincipal)

        result shouldBe defaultResponse("쿠폰이 지급 되었습니다")
    }


}