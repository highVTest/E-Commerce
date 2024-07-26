package com.highv.ecommerce.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.dto.PaymentRequest
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import com.highv.ecommerce.domain.order_master.service.OrderMasterService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlin.test.Test
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class OrderMasterServiceService {

    private val orderMasterRepository = mockk<OrderMasterRepository>()
    private val orderDetailsRepository = mockk<OrderDetailsRepository>()
    private val itemCartRepository = mockk<ItemCartRepository>()
    private val buyerRepository = mockk<BuyerRepository>()
    private val couponToBuyerRepository = mockk<CouponToBuyerRepository>()
    private val orderMasterService = OrderMasterService(
        orderMasterRepository,
        orderDetailsRepository,
        itemCartRepository,
        buyerRepository,
        couponToBuyerRepository
    )


    @Test
    fun `결제 시에 장바구니가 비었을 경우 애러 메세지 출력`(){
        val paymentRequest = PaymentRequest(
            cartIdList = arrayListOf(),
            couponIdList = arrayListOf(),
        )

        shouldThrow<RuntimeException> {
            orderMasterService.requestPayment(1L, paymentRequest)
        }.let {
            it.message shouldBe "장바구니 에서 아이템 목록을 선택해 주세요"
        }
    }

    @Test
    fun `쿠폰을 가져올 경우 유효시간 만료 시 애러 메세지 출력`(){
        val paymentRequest = PaymentRequest(
            cartIdList = arrayListOf(1),
            couponIdList = arrayListOf(1),
        )

        couponToBuyer.coupon.expiredAt = LocalDateTime.of(2021, 1, 1, 1, 0, 0)

        every { buyerRepository.findByIdOrNull(any()) } returns buyer
        every { itemCartRepository.findAllByIdAndBuyerId(any(), any()) } returns listOf(itemCart)
        every { couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(any(), any())
        } returns listOf(couponToBuyer)

        shouldThrow<RuntimeException> {
            orderMasterService.requestPayment(1L, paymentRequest)
        }.let {
            it.message shouldBe "쿠폰 유효 시간이 만료 되었습니다"
        }
    }

    @Test
    fun `결제가 제대로 이루어 졌을 때 로직 확인`(){
        val paymentRequest = PaymentRequest(
            cartIdList = arrayListOf(1),
            couponIdList = arrayListOf(1),
        )

        product1.productBackOffice = productBackOffice

        every { buyerRepository.findByIdOrNull(any()) } returns buyer
        every { itemCartRepository.findAllByIdAndBuyerId(any(), any()) } returns listOf(itemCart)
        every { couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(any(), any())
        } returns listOf(couponToBuyer)
        every { orderMasterRepository.discountTotalPriceList(1L, listOf(couponToBuyer))
        } returns mapOf( 1L to 9000 )
        every { orderMasterRepository.saveAndFlush(any()) } returns orderMaster
        every { orderDetailsRepository.saveAll(any()) } answers {
            firstArg<List<OrderDetails>>()
        }
        every { itemCartRepository.deleteAll(any()) } returns Unit


        val result = orderMasterService.requestPayment(1L, paymentRequest)
        val savedOrderDetails = slot<List<OrderDetails>>()
        verify { orderDetailsRepository.saveAll(capture(savedOrderDetails)) }

        with(savedOrderDetails.captured.first()) {
            orderMasterId shouldBe 1L
            totalPrice shouldBe 9000 // 할인된 가격 확인
        }
        productBackOffice.quantity shouldBe 99
        couponToBuyer.isUsed shouldBe true
        result shouldBe defaultResponse("주문이 완료 되었습니다, 주문 번호 : 1")

    }

    @Test
    fun `재고가 부족할 경우 재고가 부족 합니다 메세지 출력`(){
        val paymentRequest = PaymentRequest(
            cartIdList = arrayListOf(1),
            couponIdList = arrayListOf(1),
        )

        productBackOffice.quantity = 0
        product1.productBackOffice = productBackOffice

        every { buyerRepository.findByIdOrNull(any()) } returns buyer
        every { itemCartRepository.findAllByIdAndBuyerId(any(), any()) } returns listOf(itemCart)
        every { couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(any(), any())
        } returns listOf(couponToBuyer)
        every { orderMasterRepository.discountTotalPriceList(1L, listOf(couponToBuyer))
        } returns mapOf( 1L to 9000 )
        every { orderMasterRepository.saveAndFlush(any()) } returns orderMaster
        every { orderDetailsRepository.saveAll(any()) } returns  listOf(orderDetails)

        shouldThrow<RuntimeException> {
            orderMasterService.requestPayment(1L, paymentRequest)
        }.let {
            it.message shouldBe "재고가 부족 합니다"
        }


    }


    companion object{
        private fun defaultResponse(msg: String) = DefaultResponse(msg)
        private val orderMaster = OrderMaster(
            id = 1L,
            regDateTime = LocalDateTime.of(2021, 1, 1, 1, 0),
        )

        private val buyer = Buyer(
            id = 1L,
            nickname = "nickname",
            password = "password",
            email = "email",
            profileImage = "profileImage",
            phoneNumber = "1234567890",
            address = "address"
        )

        private val shop = Shop(
            sellerId = 1L,
            name = "name",
            description = "description",
            shopImage = "shopImage",
            rate = 1f
        ).apply { id = 1L }

        private val product1 = Product(
            name = "product1",
            description = "product1",
            productImage = "product1",
            createdAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            isSoldOut = false,
            shop = shop,
            categoryId = 1L,
        ).apply { id = 1L }

        private val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 10000,
            soldQuantity = 20000,
            product = product1,
        ).apply { id = 1L }

        private val orderDetails = OrderDetails(
            id = 1L,
            orderStatus = OrderStatus.ORDERED,
            complainStatus = ComplainStatus.NONE,
            buyer = buyer,
            product = product1,
            orderMasterId = 1L,
            productQuantity = 1,
            shopId = 1L,
            totalPrice = 10000
        )

        private val coupon = Coupon(
            id = 1L,
            discountPolicy = DiscountPolicy.DISCOUNT_RATE,
            discount = 10,
            quantity = 10,
            expiredAt = LocalDateTime.of(2129, 1, 1, 1, 0),
            createdAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            product = product1,
            sellerId = 1L
        )

        private val couponToBuyer = CouponToBuyer(
            id = 1L,
            coupon = coupon,
            buyer = buyer,
            isUsed = false
        )

        private val itemCart = ItemCart(
            product = product1,
            quantity = 1,
            buyerId = 1L,
            shopId = 1L
        ).apply { id = 1L }
    }

}