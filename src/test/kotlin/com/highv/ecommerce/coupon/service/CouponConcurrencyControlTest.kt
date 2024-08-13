//package com.highv.ecommerce.coupon.service
//
//import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
//import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
//import com.highv.ecommerce.domain.buyer.entity.Buyer
//import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
//import com.highv.ecommerce.domain.coupon.entity.Coupon
//import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
//import com.highv.ecommerce.domain.coupon.repository.CouponRepository
//import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
//import com.highv.ecommerce.domain.coupon.service.CouponService
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.product.repository.ProductRepository
//import com.highv.ecommerce.domain.seller.dto.ActiveStatus
//import com.highv.ecommerce.domain.seller.entity.Seller
//import com.highv.ecommerce.domain.seller.repository.SellerRepository
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
//import io.kotest.matchers.shouldBe
//import org.junit.jupiter.api.BeforeEach
//import org.redisson.api.RedissonClient
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.ActiveProfiles
//import java.time.LocalDateTime
//import java.util.concurrent.CyclicBarrier
//import java.util.concurrent.Executors
//import java.util.concurrent.TimeUnit
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//
//@SpringBootTest
//@ActiveProfiles("test")
//class CouponConcurrencyControlTest @Autowired constructor(
//    private val couponRepository: CouponRepository,
//    private val couponService: CouponService,
//    private val buyerRepository: BuyerRepository,
//    private val productRepository: ProductRepository,
//    private val productBackOfficeRepository: ProductBackOfficeRepository,
//    private val shopRepository: ShopRepository,
//    private val sellerRepository: SellerRepository,
//    private val couponToBuyerRepository: CouponToBuyerRepository,
//    private val redissonClient: RedissonClient
//) {
//
//    @BeforeEach
//    fun redisSetUp(){
//
//    }
//
//
//    @Test
//    fun `쿠폰이 발급 - 동시성 제어 테스트`(){
//
//        val executor = Executors.newFixedThreadPool(COUNT)
//        val barrier = CyclicBarrier(COUNT)
//
//        repeat(COUNT){
//            buyerRepository.save(buyer)
//        }
//
//        sellerRepository.save(seller)
//        shopRepository.save(shop)
//        val newProduct = productRepository.save(product)
//        productBackOfficeRepository.save(productBackOffice)
//        product.productBackOffice = productBackOffice
//        productRepository.saveAndFlush(newProduct)
//
//        val coupon = couponRepository.save(Coupon(
//            id = 1L,
//            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
//            discount = 10,
//            quantity = 100,
//            expiredAt = LocalDateTime.of(2030, 1, 1, 0, 0),
//            createdAt = LocalDateTime.of(2020, 1, 1, 0, 0),
//            product = newProduct,
//            sellerId = 1L,
//            couponName = "coupon",
//        ))
//
//
//        repeat(COUNT) {
//            executor.execute {
//                barrier.await()
//                couponService.issuedCoupon(coupon.id!!, buyer.id!!)
//            }
//        }
//
//        executor.awaitTermination(20, TimeUnit.SECONDS)
//
//        couponRepository.findByIdOrNull(1L)!!.quantity shouldBe 80
//    }
//
//    companion object{
//        private const val COUNT = 20
//        private val buyer = Buyer(
//            id = 1L,
//            nickname = "string",
//            email = "string@email.com",
//            password = "stringpassword",
//            profileImage = "stringprofileimage",
//            phoneNumber = "stringphone",
//            address = "stringaddress",
//        )
//
//        private val seller = Seller(
//            id = 1L,
//            nickname = "string",
//            email = "string@email.com",
//            password = "stringpassword",
//            profileImage = "stringprofileimage",
//            phoneNumber = "stringphone",
//            address = "stringaddress",
//            activeStatus = ActiveStatus.APPROVED
//        )
//
//        private val shop = Shop(
//            sellerId = 1L,
//            name = "string",
//            description = "string",
//            shopImage = "stringshopimage",
//            rate = 1f
//        )
//
//        private val product = Product(
//            name = "string",
//            description = "string",
//            productImage = "stringproductimage",
//            createdAt = LocalDateTime.of(2020, 1, 1, 0, 0),
//            updatedAt = LocalDateTime.of(2020, 1, 1, 0, 0),
//            isSoldOut = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        )
//
//        private val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 100,
//            price = 10000,
//            soldQuantity = 100,
//            product = product
//        )
//
//    }
//}