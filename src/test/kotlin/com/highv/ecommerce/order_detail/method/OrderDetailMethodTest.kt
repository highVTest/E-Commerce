//package com.highv.ecommerce.order_detail.method
//
//import com.highv.ecommerce.common.dto.DefaultResponse
//import com.highv.ecommerce.common.exception.InvalidRequestException
//import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
//import com.highv.ecommerce.domain.buyer.entity.Buyer
//import com.highv.ecommerce.domain.coupon.entity.Coupon
//import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
//import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
//import com.highv.ecommerce.domain.item_cart.entity.ItemCart
//import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
//import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
//import com.highv.ecommerce.domain.order_details.entity.OrderDetails
//import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
//import com.highv.ecommerce.domain.order_details.enumClass.ComplainType
//import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
//import com.highv.ecommerce.domain.order_master.entity.OrderMaster
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import java.time.LocalDateTime
//
//class OrderDetailMethodTest : BehaviorSpec({
//
//    afterEach {
//        clearAllMocks()
//    }
//
//    Given("buyerUpdate 메서드가 실행될 경우") {
//        When("complainType 이 EXCHANGE 이고 orderStatus 가 DELIVERED 이 아닐 경우") {
//            Then("물건 수령 전에는 교환 요청이 어렵습니다 를 반환 한다") {
//
//                orderDetails.orderStatus = OrderStatus.ORDERED
//
//                val buyerOrderStatusRequest = BuyerOrderStatusRequest(
//                    complainType = ComplainType.EXCHANGE,
//                    description = "test"
//                )
//
//                shouldThrow<InvalidRequestException> {
//                    orderDetails.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
//                }.let {
//                    it.message shouldBe "물건 수령 전에는 교환 요청이 어렵습니다"
//                }
//            }
//        }
//        When("complainType 이 REFUND 이고 orderStatus 가 DELIVERY_PREPARING 일 경우") {
//            Then("배송 준비 중에는 환불 요청이 어렵습니다 를 반환") {
//                orderDetails.orderStatus = OrderStatus.DELIVERY_PREPARING
//
//                val buyerOrderStatusRequest = BuyerOrderStatusRequest(
//                    complainType = ComplainType.REFUND,
//                    description = "test"
//                )
//
//                shouldThrow<InvalidRequestException> {
//                    orderDetails.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
//                }.let {
//                    it.message shouldBe "배송 준비 중에는 환불 요청이 어렵습니다"
//                }
//            }
//        }
//        When("complainType 이 REFUND 이고 orderStatus 가 SHIPPING 일 경우") {
//            Then("배송 중에는 환불 요청이 어렵습니다 를 반환") {
//                orderDetails.orderStatus = OrderStatus.SHIPPING
//
//                val buyerOrderStatusRequest = BuyerOrderStatusRequest(
//                    complainType = ComplainType.REFUND,
//                    description = "test"
//                )
//
//                shouldThrow<InvalidRequestException> {
//                    orderDetails.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
//                }.let {
//                    it.message shouldBe "배송 중에는 환불 요청이 어렵습니다"
//                }
//            }
//        }
//        When("complainType 이 REFUND 이고 orderStatus 가 PENDING 일 경우") {
//            Then("이미 환불 및 교환 요청이 접수 되었습니다 를 반환") {
//                orderDetails.orderStatus = OrderStatus.PENDING
//
//                val buyerOrderStatusRequest = BuyerOrderStatusRequest(
//                    complainType = ComplainType.REFUND,
//                    description = "test"
//                )
//
//                shouldThrow<InvalidRequestException> {
//                    orderDetails.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
//                }.let {
//                    it.message shouldBe "이미 환불 및 교환 요청이 접수 되었습니다"
//                }
//            }
//        }
//        When("정상적으로 실행 될 경우") {
//            Then("교환일 경우 EXCHANGE_REQUESTED 환불 일 경우 REFUND_REQUESTED 및 정상 메세지 반환") {
//                orderDetails.orderStatus = OrderStatus.ORDERED
//                val buyerOrderStatusRequest = BuyerOrderStatusRequest(
//                    complainType = ComplainType.REFUND,
//                    description = "test"
//                )
//
//                orderDetails.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
//
//                orderDetails.buyerDescription shouldBe "test"
//                orderDetails.orderStatus shouldBe OrderStatus.PENDING
//                orderDetails.complainStatus shouldBe ComplainStatus.REFUND_REQUESTED
//
//                orderDetails2.orderStatus = OrderStatus.DELIVERED
//                val buyerOrderStatusRequest2 = BuyerOrderStatusRequest(
//                    complainType = ComplainType.EXCHANGE,
//                    description = "test2"
//                )
//
//                orderDetails2.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest2)
//
//                orderDetails2.buyerDescription shouldBe "test2"
//                orderDetails2.orderStatus shouldBe OrderStatus.PENDING
//                orderDetails2.complainStatus shouldBe ComplainStatus.EXCHANGE_REQUESTED
//            }
//        }
//    }
//
//    Given("sellerUpdate 메서드가 실행이 될 경우") {
//        When("EXCHANGE_REQUESTED, REFUND_REQUESTED 일 경우") {
//            Then("EXCHANGE_REJECTED, REFUND_REJECTED 를 반환") {
//                val sellerOrderStatusRequest = SellerOrderStatusRequest(
//                    description = "test"
//                )
//
//                orderDetails.sellerUpdate(
//                    OrderStatus.PENDING,
//                    sellerOrderStatusRequest,
//                    ComplainStatus.REFUND_REQUESTED
//                )
//
//                orderDetails.complainStatus shouldBe ComplainStatus.REFUND_REJECTED
//
//                orderDetails2.sellerUpdate(
//                    OrderStatus.PENDING,
//                    sellerOrderStatusRequest,
//                    ComplainStatus.EXCHANGE_REQUESTED
//                )
//
//                orderDetails2.complainStatus shouldBe ComplainStatus.EXCHANGE_REJECTED
//
//            }
//        }
//    }
//
//}) {
//    companion object {
//        private fun defaultResponse(msg: String) = DefaultResponse(msg)
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
//            shop = shop,
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
//            shop = shop,
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
//            sellerId = 1L,
//            couponName = "test coupon name"
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
//            shop = shop
//        ).apply { id = 1L }
//    }
//}