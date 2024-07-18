//package com.highv.ecommerce.coupon.service
//
//import com.highv.ecommerce.common.dto.DefaultResponse
//import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
//import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
//import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
//import com.highv.ecommerce.domain.coupon.entity.Coupon
//import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
//import com.highv.ecommerce.domain.coupon.repository.CouponRepository
//import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
//import com.highv.ecommerce.domain.coupon.service.CouponService
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.product.repository.ProductRepository
//import com.highv.ecommerce.infra.security.UserPrincipal
//import io.kotest.matchers.shouldBe
//import io.mockk.every
//import io.mockk.mockk
//import java.time.LocalDateTime
//import kotlin.test.Test
//
//class CouponServiceTest {
//
//    private val couponRepository = mockk<CouponRepository>()
//    private val productRepository = mockk<ProductRepository>()
//    private val couponToBuyerRepository = mockk<CouponToBuyerRepository>()
//    private val buyerRepository = mockk<BuyerRepository>()
//    private val couponService = CouponService(couponRepository, productRepository, couponToBuyerRepository, buyerRepository)
//
//    //Given
//    companion object{
//        private val userPrincipal = UserPrincipal(
//            id = 1L,
//            email = "test@test.com",
//            role = "admin",
//        )
//        private val product = Product(
//            name = "Test product name",
//            description = "Test product description",
//            productImage = "test",
//            favorite = 1,
//            createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shopId = 1L,
//            categoryId = 1L,
//        )
//        private val createCouponRequest = CreateCouponRequest(
//            productId = 1,
//            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
//            discount = 30000,
//            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
//            quantity = 1
//        )
//
//        private val updateCouponRequest = UpdateCouponRequest(
//            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
//            discount = 30000,
//            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
//            quantity = 1
//        )
//
//        private val coupon = Coupon(
//            id = 1L,
//            product = product,
//            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
//            discount = 30000,
//            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
//            quantity = 1,
//            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
//            deletedAt = null,
//            isDeleted = false,
//        )
//
//        private val coupon2 = Coupon(
//            id = 2L,
//            product = product,
//            discountPolicy = DiscountPolicy.DISCOUNT_PRICE,
//            discount = 30000,
//            expiredAt = LocalDateTime.of(2024, 8, 1, 0, 0),
//            quantity = 1,
//            createdAt = LocalDateTime.of(2024, 7, 1, 0, 0),
//            deletedAt = null,
//            isDeleted = false,
//        )
//
//        fun defaultResponse(msg: String) = DefaultResponse(msg)
//    }
//
//    @Test
//    fun `쿠폰이 정상적으로 등록이 되는 지 확인`(){
//
//        every { couponRepository.save(any()) } returns coupon
//
//        val result = couponService.createCoupon(createCouponRequest, )
//
//        result shouldBe defaultResponse("쿠폰 생성이 완료 되었습니다")
//    }
//
//    @Test
//    fun `쿠폰이 정상적으로 업데이트가 되는지 확인`(){
//
//        every { couponRepository.findByIdOrNull(any()) } returns coupon
//
//        val result = couponService.updateCoupon(1L, updateCouponRequest)
//
//        result shouldBe defaultResponse("쿠폰 업데이트가 완료 되었습니다")
//    }
//
//    @Test
//    fun `쿠폰이 정상적으로 삭제가 되는지 확인`(){
//
//        every { couponRepository.findByIdOrNull(any()) } returns coupon
//        every { couponRepository.delete(any()) } returns Unit
//
//        val result = couponService.deleteCoupon(1L)
//
//        result shouldBe defaultResponse("쿠폰 삭제가 완료 되었습니다")
//    }
//
//    @Test
//    fun `판메자가 쿠폰을 단건 조회 할 수 있게 확인`(){
//        every { couponRepository.findByIdOrNull(any()) } returns coupon
//
//        val result = couponService.getSellerCouponById(1L)
//
//
//        result.productId shouldBe 1L
//        result.expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
//        result.discountRate shouldBe 50
//        result.discountPrice shouldBe null
//    }
//
//    @Test
//    fun `판메자가 쿠폰을 여러건 조회 할 수 있게 확인`(){
//
//        every { couponRepository.findAll() } returns listOf(coupon, coupon2)
//
//        val result = couponService.getSellerCouponList()
//
//
//        result!![0].productId shouldBe 1L
//        result[0].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
//        result[0].discountRate shouldBe 50
//        result[0].discountPrice shouldBe null
//
//        result[1].productId shouldBe 2L
//        result[1].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
//        result[1].discountRate shouldBe null
//        result[1].discountPrice shouldBe 1000
//    }
//
//
//    @Test
//    fun `구메자가 쿠폰을 단건 조회 할 수 있게 확인`(){
//        every { couponRepository.findByIdOrNull(any()) } returns coupon
//
//        val result = couponService.getSellerCouponById(1L)
//
//
//        result.productId shouldBe 1L
//        result.expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
//        result.discountRate shouldBe 50
//        result.discountPrice shouldBe null
//    }
//
//
//    @Test
//    fun `구메자가 쿠폰을 여러건 조회 할 수 있게 확인`(){
//
//        every { couponRepository.findAll() } returns listOf(coupon, coupon2)
//
//        val result = couponService.getSellerCouponList()
//
//
//        result!![0].productId shouldBe 1L
//        result[0].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
//        result[0].discountRate shouldBe 50
//        result[0].discountPrice shouldBe null
//
//        result[1].productId shouldBe 2L
//        result[1].expiredAt shouldBe LocalDateTime.of(2024, 8, 1, 0, 0)
//        result[1].discountRate shouldBe null
//        result[1].discountPrice shouldBe 1000
//    }
//
//
//
//}