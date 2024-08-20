package com.highv.ecommerce.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.InvalidCouponDiscountException
import com.highv.ecommerce.common.exception.UnauthorizedUserException
import com.highv.ecommerce.common.innercall.TxAdvice
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
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
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.infra.security.UserPrincipal
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.LocalDateTime
import kotlin.test.Test

class CouponServiceTest {

    private val redisTemplate = mockk<RedisTemplate<String, String>>()
    private val couponRepository = mockk<CouponRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val couponToBuyerRepository = mockk<CouponToBuyerRepository>()
    private val redissonClient = mockk<RedissonClient>()
    private val userPrincipal = mockk<UserPrincipal>()
    private val txAdvice = mockk<TxAdvice>()
    private val couponService = CouponService(
        couponRepository, productRepository, couponToBuyerRepository,
        txAdvice, redissonClient, redisTemplate
    )
    private val valueOperations = mockk<ValueOperations<String, String>>()

    init {
        every { redisTemplate.opsForValue() } returns valueOperations

    }

//    @Test
//    fun `쿠폰의 정책이 DISCOUNT_RATE 이고 CreateCouponRequest 의 값이 100 이상일 경우 InvalidDiscountPolicyException을 벌생`() {
//
//        createCouponRequest.discountPolicy = DiscountPolicy.DISCOUNT_RATE
//        createCouponRequest.discount = 100
//
//        shouldThrow<InvalidCouponDiscountException> {
//            couponService.createCoupon(createCouponRequest, userPrincipal.id)
//        }.let {
//            it.message shouldBe "할인율은 40%를 넘길 수 없습니다"
//        }
//    }

    @Test
    fun `쿠폰이 정상적으로 등록이 되는 지 확인`() {
        product.productBackOffice = productBackOffice
        createCouponRequest.discountPolicy = DiscountPolicy.DISCOUNT_PRICE
        createCouponRequest.discount = 3000

        every { userPrincipal.id } returns 1
        every { productRepository.findByIdOrNull(any()) } returns product
        every { couponRepository.existsByProductId(any()) } returns false
        every { couponRepository.save(any()) } returns coupon

        val result = couponService.createCoupon(createCouponRequest, userPrincipal.id)

        result shouldBe defaultResponse("쿠폰 생성이 완료 되었습니다")
    }

    @Test
    fun `쿠폰 생성 시에 가격 정책이 일 때 할인 가격이 40% 을 넘으면 InvalidCouponDiscountException 를 Throw`() {
        product.productBackOffice = productBackOffice
        createCouponRequest.discountPolicy = DiscountPolicy.DISCOUNT_PRICE
        createCouponRequest.discount = 4500

        every { userPrincipal.id } returns 1
        every { productRepository.findByIdOrNull(any()) } returns product

        shouldThrow<InvalidCouponDiscountException> {
            couponService.createCoupon(createCouponRequest, userPrincipal.id)
        }.let {
            it.message shouldBe "최대 가격 할인율은 현재 상품 가격의 40% 입니다"
        }
    }

    @Test
    fun `쿠폰이 정상적으로 업데이트가 되는지 확인`() {
        val updateCouponRequest = UpdateCouponRequest(
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 2500,
            expiredAt = LocalDateTime.of(2029, 8, 1, 0, 0),
            quantity = 1
        )

        product.productBackOffice = productBackOffice

        every { userPrincipal.id } returns 1

        every { couponRepository.findByIdOrNull(any()) } returns coupon

        val result = couponService.updateCoupon(1L, updateCouponRequest, userPrincipal.id)

        result shouldBe defaultResponse("쿠폰 업데이트가 완료 되었습니다")
    }

    @Test
    fun `쿠폰 업데이트 시에 가격 정책이 일 때 할인 가격이 40% 을 넘으면 InvalidCouponDiscountException 를 Throw`() {
        val updateCouponRequest = UpdateCouponRequest(
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 5000,
            expiredAt = LocalDateTime.of(2029, 8, 1, 0, 0),
            quantity = 1
        )

        product.productBackOffice = productBackOffice

        every { userPrincipal.id } returns 1
        every { couponRepository.findByIdOrNull(any()) } returns coupon

        shouldThrow<InvalidCouponDiscountException> {
            couponService.updateCoupon(1L,updateCouponRequest, userPrincipal.id)
        }.let {
            it.message shouldBe "최대 가격 할인율은 현재 상품 가격의 40% 입니다"
        }
    }

    @Test
    fun `쿠폰이 다른 사용자가 업데이트 하려고 할 경우 UnauthorizedException`() {
        product.productBackOffice = productBackOffice

        val updateCouponRequest = UpdateCouponRequest(
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 2500,
            expiredAt = LocalDateTime.of(2029, 8, 1, 0, 0),
            quantity = 1
        )

        every { userPrincipal.id } returns 2

        every { couponRepository.findByIdOrNull(any()) } returns coupon

        shouldThrow<UnauthorizedUserException> {
            couponService.updateCoupon(1L, updateCouponRequest, userPrincipal.id)
        }.let {
            it.message shouldBe "다른 사용자는 해당 쿠폰을 수정할 수 없습니다"
        }
    }

    @Test
    fun `판메자가 쿠폰을 단건 조회 할 수 있게 확인`() {

        coupon.product.id = 1L
        every { userPrincipal.id } returns 1
        every { couponRepository.findByIdAndSellerId(any(), any()) } returns coupon

        val result = couponService.getSellerCouponById(1L, userPrincipal.id)


        result.productId shouldBe 1L
        result.expiredAt shouldBe LocalDateTime.of(2029, 8, 1, 0, 0)
        result.discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result.discount shouldBe 30000
    }

    @Test
    fun `판메자가 쿠폰을 여러건 조회 할 수 있게 확인`() {

        coupon.product.id = 1L
        coupon2.product.id = 1L

        every { userPrincipal.id } returns 1
        every { couponRepository.findAllBySellerId(any()) } returns listOf(coupon, coupon2)

        val result = couponService.getSellerCouponList(userPrincipal.id)


        result.size shouldBe 2

        result[0].productId shouldBe 1L
        result[0].expiredAt shouldBe LocalDateTime.of(2029, 8, 1, 0, 0)
        result[0].discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result[0].discount shouldBe 30000

        result[1].productId shouldBe 1L
        result[1].expiredAt shouldBe LocalDateTime.of(2029, 8, 1, 0, 0)
        result[1].discountPolicy shouldBe DiscountPolicy.DISCOUNT_RATE.name
        result[1].discount shouldBe 50
    }

    @Test
    fun `구메자가 쿠폰을 단건 조회 할 수 있게 확인`() {

        coupon.discount = 30000

        coupon.product.id = 1L
        every { userPrincipal.id } returns 1
        every { couponRepository.findByIdAndSellerId(any(), any()) } returns coupon
        every { couponToBuyerRepository.findByProductIdAndBuyerId(any(), any()) } returns couponToBuyer
        every { couponRepository.findByIdOrNull(any()) } returns coupon

        val result = couponService.getSellerCouponById(1L, userPrincipal.id)


        result.productId shouldBe 1L
        result.expiredAt shouldBe LocalDateTime.of(2029, 8, 1, 0, 0)
        result.discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result.discount shouldBe 30000
    }

    @Test
    fun `판매자가 쿠폰을 여러건 조회 할 수 있게 확인`() {

        coupon.product.id = 1L
        coupon2.product.id = 1L

        every { userPrincipal.id } returns 1
        every { couponRepository.findAllBySellerId(any()) } returns listOf(coupon, coupon2)
        val result = couponService.getSellerCouponList(userPrincipal.id)


        result[0].productId shouldBe 1L
        result[0].expiredAt shouldBe LocalDateTime.of(2029, 8, 1, 0, 0)
        result[0].discountPolicy shouldBe DiscountPolicy.DISCOUNT_PRICE.name
        result[0].discount shouldBe 30000

        result[1].productId shouldBe 1L
        result[1].expiredAt shouldBe LocalDateTime.of(2029, 8, 1, 0, 0)
        result[1].discountPolicy shouldBe DiscountPolicy.DISCOUNT_RATE.name
        result[1].discount shouldBe 50
    }
    //Given
    companion object {
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
        private var createCouponRequest = CreateCouponRequest(
            productId = 1,
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = LocalDateTime.of(2029, 8, 1, 0, 0),
            quantity = 1,
            couponName = "test"
        )



        private val coupon = Coupon(
            id = 1L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
            discount = 30000,
            expiredAt = LocalDateTime.of(2029, 8, 1, 0, 0),
            quantity = 1000,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            sellerId = 1L,
            couponName = "test"
        )

        private val coupon2 = Coupon(
            id = 2L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 50,
            expiredAt = LocalDateTime.of(2029, 8, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            sellerId = 1L,
            couponName = "test2"
        )

        private val coupon3 = Coupon(
            id = 3L,
            product = product,
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 50,
            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
            quantity = 1,
            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
            sellerId = 2L,
            couponName = "test3"
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
            buyerId = buyer1.id!!,
            isUsed = false
        )

        val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 10,
            product = product,
            price = 10000,
            soldQuantity = 1000
        )

        fun defaultResponse(msg: String) = DefaultResponse(msg)
    }
}