package com.highv.ecommerce.order_detail.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.innercall.TxAdvice
import com.highv.ecommerce.common.lock.service.RedisLockService
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderResponse
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_details.service.OrderDetailsService
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

class BuyerOrderServiceTest : BehaviorSpec() {

    init {
        val orderDetailsRepository = mockk<OrderDetailsRepository>()
        val couponToBuyerRepository = mockk<CouponToBuyerRepository>()
        val couponRepository = mockk<CouponRepository>()
        val orderMasterRepository = mockk<OrderMasterRepository>()
        val lockService = mockk<RedisLockService>()
        val txAdvice = mockk<TxAdvice>()

        val orderDetailsService =
            OrderDetailsService(
                orderDetailsRepository,
                couponToBuyerRepository,
                couponRepository,
                orderMasterRepository,
                lockService,
                txAdvice
            )

        Given("주문 내역이 존재하면") {
            val buyerId = 1L

            val buyer = Buyer(
                id = buyerId,
                nickname = "testNickname",
                password = "testPassword",
                email = "test@email.com",
                profileImage = "testImage",
                phoneNumber = "010-xxxx-xxxx",
                address = "testAddress"
            )

            // 가게 3개 생성
            val shop = (1..3).map {
                Shop(
                    sellerId = it.toLong(),
                    name = "testName $it",
                    description = "testDescription $it",
                    shopImage = "testImage $it",
                    rate = it.toFloat()
                ).apply { id = it.toLong() }
            }

            // 가게 1번의 상품 3개
            val product1 = (1..3).map {
                Product(
                    name = "Test Product $it",
                    description = "Test Description $it",
                    productImage = "image.jpg $it",
                    createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
                    updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
                    isSoldOut = false,
                    deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
                    isDeleted = false,
                    shop = shop[0],
                    categoryId = 1L,
                ).apply { id = it.toLong() }
            }

            // 가게 2번의 상품 2개
            val product2 = (4..5).map {
                Product(
                    name = "Test Product $it",
                    description = "Test Description $it",
                    productImage = "image.jpg $it",
                    createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
                    updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
                    isSoldOut = false,
                    deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
                    isDeleted = false,
                    shop = shop[1],
                    categoryId = 1L
                ).apply { id = it.toLong() }
            }

            // 가게 3번의 상품 1개
            val product3 = Product(
                name = "Test Product 6",
                description = "Test Description 6",
                productImage = "image.jpg 6",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
                updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
                isSoldOut = false,
                deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
                isDeleted = false,
                shop = shop[2],
                categoryId = 1L
            ).apply { id = 6 }

            // 상품과 백 오피스 연결하기
            val productBackOffice1: List<ProductBackOffice> = product1.map {
                ProductBackOffice(
                    quantity = it.id!!.toInt() * 15,
                    price = it.id!!.toInt() * 1000,
                    soldQuantity = 0,
                    product = it
                ).apply { id = it.id }
            }

            val productBackOffice2: List<ProductBackOffice> = product2.map {
                ProductBackOffice(
                    quantity = it.id!!.toInt() * 15,
                    price = it.id!!.toInt() * 1000,
                    soldQuantity = 0,
                    product = it
                ).apply { id = it.id }
            }

            val productBackOffice3: ProductBackOffice = ProductBackOffice(
                quantity = product3.id!!.toInt() * 15,
                price = product3.id!!.toInt() * 1000,
                soldQuantity = 0,
                product = product3
            ).apply { id = product3.id }

            for (i in 0..2) {
                product1[i].productBackOffice = productBackOffice1[i]
            }

            for (i in 0..1) {
                product2[i].productBackOffice = productBackOffice2[i]
            }
            product3.productBackOffice = productBackOffice3



            When("구매자가 주문 내역 전체 조회 시") {

                // 주문 내역 만들기
                var orderDetailId = 1L

                val orderMasters = (1..2).map {
                    OrderMaster(
                        id = it.toLong(),
                        regDateTime = LocalDateTime.of(2021, 1, it, it + 2, 0, 0),
                    )
                }

                val orderDetails: MutableList<OrderDetails> = mutableListOf()

                // 1번 내역에 상품 담기
                // 가게 1, 2번 상품만 존재
                product1.forEach {
                    orderDetails.add(
                        OrderDetails(
                            id = orderDetailId++,
                            orderStatus = OrderStatus.DELIVERED,
                            complainStatus = ComplainStatus.NONE,
                            buyerDateTime = null,
                            buyerDescription = null,
                            sellerDateTime = null,
                            sellerDescription = null,
                            buyer = buyer,
                            product = it,
                            orderMasterId = orderMasters[0].id!!,
                            productQuantity = 10,
                            shop = it.shop,
                            totalPrice = 5000
                        )
                    )
                }

                product2.forEach {
                    orderDetails.add(
                        OrderDetails(
                            id = orderDetailId++,
                            orderStatus = OrderStatus.SHIPPING,
                            complainStatus = ComplainStatus.NONE,
                            buyerDateTime = null,
                            buyerDescription = null,
                            sellerDateTime = null,
                            sellerDescription = null,
                            buyer = buyer,
                            product = it,
                            orderMasterId = orderMasters[0].id!!,
                            productQuantity = 10,
                            shop = it.shop,
                            totalPrice = 5000
                        )
                    )
                }

                // 2번 내역에 상품 담기
                // 가게 2, 3번 상품만 존재
                product2.forEach {
                    orderDetails.add(
                        OrderDetails(
                            id = orderDetailId++,
                            orderStatus = OrderStatus.ORDERED,
                            complainStatus = ComplainStatus.NONE,
                            buyerDateTime = null,
                            buyerDescription = null,
                            sellerDateTime = null,
                            sellerDescription = null,
                            buyer = buyer,
                            product = it,
                            orderMasterId = orderMasters[1].id!!,
                            productQuantity = 10,
                            shop = it.shop,
                            totalPrice = 5000
                        )
                    )
                }

                orderDetails.add(
                    OrderDetails(
                        id = orderDetailId++,
                        orderStatus = OrderStatus.ORDERED,
                        complainStatus = ComplainStatus.NONE,
                        buyerDateTime = null,
                        buyerDescription = null,
                        sellerDateTime = null,
                        sellerDescription = null,
                        buyer = buyer,
                        product = product3,
                        orderMasterId = orderMasters[1].id!!,
                        productQuantity = 10,
                        shop = product3.shop,
                        totalPrice = 5000
                    )
                )

                every { orderDetailsRepository.findAllByBuyerId(buyerId) } returns orderDetails
                every { orderMasterRepository.findByIdInOrderByIdDesc(any()) } returns orderMasters.sortedByDescending { it.id }

                val response: List<BuyerOrderResponse> = orderDetailsService.getBuyerOrders(buyerId)

                Then("주문 목록이 반환된다.") {
                    response.size shouldBe 2 // 총 주문 개수

                    // 주문 목록 최신순
                    response[0].orderMasterId shouldBe 2L
                    response[1].orderMasterId shouldBe 1L

                    // 주문 1번 내역 검증
                    response[1].orderShopDetails.size shouldBe 2 // 1번 주문의 가게 수
                    response[1].orderShopDetails[0].shopId shouldBe 1L
                    response[1].orderShopDetails[1].shopId shouldBe 2L
                    // 주문 2번 내역 검증
                    response[0].orderShopDetails.size shouldBe 2 // 2번 주문의 가게 수
                    response[0].orderShopDetails[0].shopId shouldBe 2L
                    response[0].orderShopDetails[1].shopId shouldBe 3L

                }
            }

            When("주문 내역 단건 조회 시") {
                // 주문 내역 만들기
                var orderDetailId = 1L
                val orderMaster = OrderMaster(
                    id = 1L,
                    regDateTime = LocalDateTime.of(2021, 1, 1, 1, 0, 0),
                )

                val orderDetails: MutableList<OrderDetails> = mutableListOf()

                product1.forEach {
                    orderDetails.add(
                        OrderDetails(
                            id = orderDetailId++,
                            orderStatus = OrderStatus.ORDERED,
                            complainStatus = ComplainStatus.NONE,
                            buyerDateTime = null,
                            buyerDescription = null,
                            sellerDateTime = null,
                            sellerDescription = null,
                            buyer = buyer,
                            product = it,
                            orderMasterId = orderMaster.id!!,
                            productQuantity = 10,
                            shop = it.shop,
                            totalPrice = 5000
                        )
                    )
                }

                product2.forEach {
                    orderDetails.add(
                        OrderDetails(
                            id = orderDetailId++,
                            orderStatus = OrderStatus.ORDERED,
                            complainStatus = ComplainStatus.NONE,
                            buyerDateTime = null,
                            buyerDescription = null,
                            sellerDateTime = null,
                            sellerDescription = null,
                            buyer = buyer,
                            product = it,
                            orderMasterId = orderMaster.id!!,
                            productQuantity = 10,
                            shop = it.shop,
                            totalPrice = 5000
                        )
                    )
                }

                orderDetails.add(
                    OrderDetails(
                        id = orderDetailId++,
                        orderStatus = OrderStatus.ORDERED,
                        complainStatus = ComplainStatus.NONE,
                        buyerDateTime = null,
                        buyerDescription = null,
                        sellerDateTime = null,
                        sellerDescription = null,
                        buyer = buyer,
                        product = product3,
                        orderMasterId = orderMaster.id!!,
                        productQuantity = 10,
                        shop = product3.shop,
                        totalPrice = 5000
                    )
                )

                every { orderMasterRepository.findByIdOrNull(any()) } returns orderMaster
                every {
                    orderDetailsRepository.findAllByBuyerIdAndOrderMasterId(
                        buyerId,
                        orderMaster.id!!
                    )
                } returns orderDetails

                val response: BuyerOrderResponse = orderDetailsService.getBuyerOrderDetails(buyerId, orderMaster.id!!)

                Then("주문 내역이 반환된다.") {
                    // 주문 내역 가게 수
                    response.orderShopDetails.size shouldBe 3

                    // 주문 내역 가게 별 검증
                    // 1번 가게
                    response.orderShopDetails[0].shopId shouldBe 1L
                    response.orderShopDetails[0].productsOrders.size shouldBe 3

                    // 2번 가게
                    response.orderShopDetails[1].shopId shouldBe 2L
                    response.orderShopDetails[1].productsOrders.size shouldBe 2

                    // 3번 가게
                    response.orderShopDetails[2].shopId shouldBe 3L
                    response.orderShopDetails[2].productsOrders.size shouldBe 1

                }

            }
        }

        Given("주문 내역이 존재하지 않으면") {
            val buyerId = 1L
            val orderMasterId = 99L

            When("단 건 조회 시") {
                every { orderMasterRepository.findByIdOrNull(orderMasterId) } returns null

                Then("예외가 발생한다.") {
                    shouldThrow<CustomRuntimeException> {
                        orderDetailsService.getBuyerOrderDetails(
                            buyerId,
                            orderMasterId
                        )
                    }
                        .also {
                            it.message shouldBe "주문 내역이 없습니다."
                        }
                }

                every {
                    orderDetailsRepository.findAllByBuyerIdAndOrderMasterId(
                        buyerId,
                        orderMasterId
                    )
                } returns emptyList()

                Then("예외가 발생한다.") {
                    shouldThrow<CustomRuntimeException> {
                        orderDetailsService.getBuyerOrderDetails(
                            buyerId,
                            orderMasterId
                        )
                    }
                        .also {
                            it.message shouldBe "주문 내역이 없습니다."
                        }
                }
            }
        }
    }
}