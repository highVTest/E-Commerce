//package com.highv.ecommerce.order_detail.service
//
//import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
//import com.highv.ecommerce.domain.buyer.entity.Buyer
//import com.highv.ecommerce.domain.coupon.entity.Coupon
//import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
//import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
//import com.highv.ecommerce.domain.coupon.repository.CouponRepository
//import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
//import com.highv.ecommerce.domain.item_cart.entity.ItemCart
//import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
//import com.highv.ecommerce.domain.order_details.dto.OrderStatusResponse
//import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
//import com.highv.ecommerce.domain.order_details.entity.OrderDetails
//import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
//import com.highv.ecommerce.domain.order_details.enumClass.ComplainType
//import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
//import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
//import com.highv.ecommerce.domain.order_details.service.OrderDetailsService
//import com.highv.ecommerce.domain.order_master.entity.OrderMaster
//import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.mockk
//import java.time.LocalDateTime
//import kotlin.test.Test
//
//class OrderDetailsServiceTest: BehaviorSpec(){
//    private val orderMasterRepository = mockk<OrderMasterRepository>()
//    private val orderDetailsRepository = mockk<OrderDetailsRepository>()
//    private val couponToBuyerRepository = mockk<CouponToBuyerRepository>()
//    private val couponRepository = mockk<CouponRepository>()
//    private val orderDetailsService = OrderDetailsService(
//        orderDetailsRepository,
//        couponToBuyerRepository,
//        couponRepository,
//        orderMasterRepository
//    )
//
//    init {
//
//        Given("requestComplainAccept 메서드를 호출 시"){
//            When("환불을 승인할 경우"){
//                Then("주문이 취소되고 쿠폰이 사용자 한테 반환 된다 또한 재고도 반환 된다"){
//                    product1.productBackOffice = productBackOffice
//
//                    val sellerOrderStatusRequest = SellerOrderStatusRequest(
//                        buyerId = 1L,
//                        description = "test"
//                    )
//                    orderDetails.complainStatus = ComplainStatus.REFUND_REQUESTED
//
//                    every {
//                        orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(any(), any(), any())
//                    } returns listOf(orderDetails)
//                    every { couponRepository.findAllByProductId(any()) } returns listOf(coupon.id!!)
//                    every { couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedTrue(any(),any())
//                    } returns listOf(couponToBuyer)
//                    every { orderDetailsRepository.saveAll(any()) } returns listOf(orderDetails)
//
//                    val result = orderDetailsService.requestComplainAccept(1L, 1L, sellerOrderStatusRequest)
//
//                    orderDetails.orderStatus shouldBe OrderStatus.ORDER_CANCELED
//                    orderDetails.complainStatus shouldBe ComplainStatus.REFUNDED
//                    orderDetails.sellerDescription shouldBe "test"
//                    productBackOffice.quantity shouldBe 101
//                    couponToBuyer.isUsed shouldBe false
//
//                    result shouldBe OrderStatusResponse.from(ComplainType.REFUND, "전체 요청 승인 완료 되었습니다")
//                }
//            }
//            When("교환을 승인할 경우"){
//                Then("주문 상태가 주문 준비 중으로 변경 된다"){
//
//                    val sellerOrderStatusRequest = SellerOrderStatusRequest(
//                        buyerId = 1L,
//                        description = "test"
//                    )
//                    orderDetails.complainStatus = ComplainStatus.EXCHANGE_REQUESTED
//
//                    every {
//                        orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(any(), any(), any())
//                    } returns listOf(orderDetails)
//                    every { couponRepository.findAllByProductId(any()) } returns listOf(coupon.id!!)
//                    every { couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedTrue(any(),any())
//                    } returns listOf(couponToBuyer)
//                    every { orderDetailsRepository.saveAll(any()) } returns listOf(orderDetails)
//
//                    val result = orderDetailsService.requestComplainAccept(1L, 1L, sellerOrderStatusRequest)
//
//                    orderDetails.orderStatus shouldBe OrderStatus.PRODUCT_PREPARING
//                    orderDetails.complainStatus shouldBe ComplainStatus.EXCHANGED
//                    result shouldBe OrderStatusResponse.from(ComplainType.EXCHANGE, "전체 요청 승인 완료 되었습니다")
//
//                }
//            }
//            When("교환과 환불 모두 하지 않은 경우"){
//                Then("구매자가 환불 및 교환 요청을 하지 않았 거나 요청 처리가 완료 되었습니다 를 반환"){
//                    val sellerOrderStatusRequest = SellerOrderStatusRequest(
//                        buyerId = 1L,
//                        description = "test"
//                    )
//                    orderDetails.complainStatus = ComplainStatus.NONE
//
//                    every {
//                        orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(any(), any(), any())
//                    } returns listOf(orderDetails)
//                    every { couponRepository.findAllByProductId(any()) } returns listOf(coupon.id!!)
//                    every { couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedTrue(any(),any())
//                    } returns listOf(couponToBuyer)
//                    every { orderDetailsRepository.saveAll(any()) } returns listOf(orderDetails)
//
//                    shouldThrow<RuntimeException> {
//                        orderDetailsService.requestComplainAccept(1L, 1L, sellerOrderStatusRequest)
//                    }.let {
//                        it.message shouldBe "구매자가 환불 및 교환 요청을 하지 않았 거나 요청 처리가 완료 되었습니다"
//                    }
//                }
//            }
//        }
//
//        afterEach {
//            clearAllMocks()
//        }
//    }
//
//
//    @Test
//    fun `buyerRequestComplain 메서드로 바이어가 컴플레인을 걸었을 경우 정상적으로 작동하는지 확인`(){
//
//        val buyerOrderStatusRequest = BuyerOrderStatusRequest(
//            complainType = ComplainType.REFUND,
//            description = "dummy description",
//        )
//
//        every { orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(any(), any(), any())
//        } returns listOf(orderDetails, orderDetails2)
//
//        every { orderDetailsRepository.saveAll(any())
//        } returns listOf(orderDetails, orderDetails2)
//
//
//        val result = orderDetailsService.buyerRequestComplain(buyerOrderStatusRequest,1L,1L,1L)
//
//        orderDetails.orderStatus shouldBe OrderStatus.PENDING
//        orderDetails.complainStatus shouldBe ComplainStatus.REFUND_REQUESTED
//        orderDetails.buyerDescription shouldBe "dummy description"
//        orderDetails2.orderStatus shouldBe OrderStatus.PENDING
//        orderDetails2.complainStatus shouldBe ComplainStatus.REFUND_REQUESTED
//        orderDetails2.buyerDescription shouldBe "dummy description"
//        result shouldBe OrderStatusResponse.from(ComplainType.REFUND, "요청 완료 되었습니다")
//
//    }
//
//    @Test
//    fun `requestComplainReject 메서드로 셀러가 컴플레인을 거절 었을 경우 정상적으로 작동 하는지 확인`(){
//        orderDetails.complainStatus = ComplainStatus.REFUND_REQUESTED
//
//        val sellerOrderStatusRequest = SellerOrderStatusRequest(
//            buyerId = 1L,
//            description = "dummy description",
//        )
//
//        every {
//            orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(any(), any(), any())
//        } returns listOf(orderDetails)
//
//        every {
//            orderDetailsRepository.saveAll(any())
//        } returns listOf(orderDetails)
//
//        val result = orderDetailsService.requestComplainReject(sellerOrderStatusRequest, 1L, 1L)
//
//        orderDetails.orderStatus shouldBe OrderStatus.ORDERED
//        orderDetails.complainStatus shouldBe ComplainStatus.REFUND_REJECTED
//        orderDetails.sellerDescription shouldBe "dummy description"
//        result shouldBe OrderStatusResponse.from(ComplainType.REFUND, "전체 요청 거절 완료 되었습니다")
//    }
//
//    @Test
//    fun `getSellerOrderDetailsAll 메서드 실행 시 Seller 가 주문 정보를 모두 조회`(){
//
//        every { orderDetailsRepository.findAllByShopId(1L) } returns listOf(orderDetails, orderDetails2)
//
//        val result = orderDetailsService.getSellerOrderDetailsAll(1L, 1L)
//
//        result.size shouldBe 2
//    }
//
//    @Test
//    fun `getSellerOrderDetailsBuyer 메서드 실행 시 Seller 가 buyer의 주문 정보를 모두 조회`(){
//
//        every { orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(any(), any(), any()) } returns listOf(orderDetails, orderDetails2)
//
//        val result = orderDetailsService.getSellerOrderDetailsBuyer(1L, 1L, 1L)
//
//        result.size shouldBe 2
//    }
//
//
//
//    companion object{
//        private val orderMaster = OrderMaster(
//            id = 1L,
//            regDateTime = LocalDateTime.of(2021, 1, 1, 1, 0),
//        )
//
//        private val buyer = Buyer(
//            id = 1L,
//            nickname = "nickname",
//            password = "password",
//            email = "email",
//            profileImage = "profileImage",
//            phoneNumber = "1234567890",
//            address = "address"
//        )
//
//        private val shop = Shop(
//            sellerId = 1L,
//            name = "name",
//            description = "description",
//            shopImage = "shopImage",
//            rate = 1f
//        ).apply { id = 1L }
//
//        private val product1 = Product(
//            name = "product1",
//            description = "product1",
//            productImage = "product1",
//            createdAt = LocalDateTime.of(2021, 1, 1, 1, 0),
//            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 0),
//            isSoldOut = false,
//            shop = shop,
//            categoryId = 1L,
//        ).apply { id = 1L }
//
//        private val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 100,
//            price = 10000,
//            soldQuantity = 20000,
//            product = product1,
//        ).apply { id = 1L }
//
//        private val orderDetails = OrderDetails(
//            id = 1L,
//            orderStatus = OrderStatus.ORDERED,
//            complainStatus = ComplainStatus.NONE,
//            buyer = buyer,
//            product = product1,
//            orderMasterId = 1L,
//            productQuantity = 1,
//            shopId = 1L,
//            totalPrice = 10000
//        )
//
//        private val orderDetails2 = OrderDetails(
//            id = 2L,
//            orderStatus = OrderStatus.ORDERED,
//            complainStatus = ComplainStatus.NONE,
//            buyer = buyer,
//            product = product1,
//            orderMasterId = 1L,
//            productQuantity = 1,
//            shopId = 1L,
//            totalPrice = 10000
//        )
//
//        private val coupon = Coupon(
//            id = 1L,
//            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
//            discount = 10,
//            quantity = 10,
//            expiredAt = LocalDateTime.of(2129, 1, 1, 1, 0),
//            createdAt = LocalDateTime.of(2021, 1, 1, 1, 0),
//            product = product1,
//            sellerId = 1L
//        )
//
//        private val couponToBuyer = CouponToBuyer(
//            id = 1L,
//            coupon = coupon,
//            buyer = buyer,
//            isUsed = false
//        )
//
//        private val itemCart = ItemCart(
//            product = product1,
//            quantity = 1,
//            buyerId = 1L,
//            shopId = 1L
//        ).apply { id = 1L }
//    }
//}