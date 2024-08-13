//package com.highv.ecommerce.item_cart
//
//import com.highv.ecommerce.common.exception.InvalidQuantityException
//import com.highv.ecommerce.common.exception.ItemNotFoundException
//import com.highv.ecommerce.common.exception.ProductNotFoundException
//import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
//import com.highv.ecommerce.domain.item_cart.dto.request.SelectProductQuantity
//import com.highv.ecommerce.domain.item_cart.entity.ItemCart
//import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
//import com.highv.ecommerce.domain.item_cart.service.ItemCartService
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.product.repository.ProductRepository
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import java.time.LocalDateTime
//
//class ItemCartServiceTest : BehaviorSpec() {
//
//    init {
//
//        val itemCartRepository = mockk<ItemCartRepository>(relaxed = true)
//        val productRepository = mockk<ProductRepository>(relaxed = true)
//        val itemCartService: ItemCartService = ItemCartService(itemCartRepository, productRepository)
//
//        afterEach {
//            clearAllMocks()
//        }
//
//        this.Given("상품을 장바구니에 담을 때") {
//
//            val buyerId = 1L
//
//            val shop = Shop(
//                sellerId = 1L,
//                name = "testName",
//                description = "testDescription",
//                shopImage = "testImage",
//                rate = 0.0f
//            ).apply { id = 1L }
//
//            val product = Product(
//                name = "Test Product",
//                description = "Test Description",
//                productImage = "image.jpg",
//                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//                updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
//                isSoldOut = false,
//                deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
//                isDeleted = false,
//                shop = shop,
//                categoryId = 1L
//            ).apply { id = 1L }
//
//
//            When("상품 수량이 1개 이상이면") {
//
//                val request: SelectProductQuantity = SelectProductQuantity(quantity = 5)
//
//                val itemCart = ItemCart(
//                    product = product,
//                    quantity = request.quantity,
//                    buyerId = buyerId,
//                    shop = shop
//                )
//
//                every { productRepository.findByIdOrNull(product.id!!) } returns product
//
//                every { itemCartRepository.findByProductIdAndBuyerId(product.id!!, buyerId) } returns null
//
//                every { itemCartRepository.save(any()) } returns itemCart.apply { id = 1L }
//
//
//                Then("장바구니에 추가된다.") {
//                    val response = itemCartService.addItemIntoCart(product.id!!, request, buyerId)
//
//                    response.msg shouldBe "장바구니에 상품이 추가됐습니다."
//
//                    verify(exactly = 1) { productRepository.findByIdOrNull(product.id!!) }
//                    verify(exactly = 1) { itemCartRepository.findByProductIdAndBuyerId(product.id!!, buyerId) }
//                    verify(exactly = 1) { itemCartRepository.save(any()) }
//                }
//
//            }
//
//            When("상품 수량이 1개보다 적으면") {
//
//                val request: SelectProductQuantity = SelectProductQuantity(quantity = 0)
//
//                Then("상품 개수 예외가 발생한다.") {
//                    shouldThrow<InvalidQuantityException> {
//                        itemCartService.addItemIntoCart(product.id!!, request, buyerId)
//                    }.let {
//                        it.message shouldBe "상품의 수량이 1개보다 적을 수 없습니다."
//                    }
//                }
//            }
//
//            When("존재하지 않는 상품일 때") {
//
//                val request: SelectProductQuantity = SelectProductQuantity(quantity = 1)
//
//                every { productRepository.findByIdOrNull(any()) } returns null
//
//                Then("상품이 없다는 예외가 발생한다.") {
//                    shouldThrow<ProductNotFoundException> { itemCartService.addItemIntoCart(1L, request, buyerId) }
//                        .let {
//                            it.message shouldBe "해당 상품이 존재하지 않습니다."
//                        }
//                }
//            }
//
//            When("담는 수량이 1개 이상이고 이미 장바구니에 있는 상품이면") {
//
//                val request: SelectProductQuantity = SelectProductQuantity(quantity = 5)
//
//                val itemCart = ItemCart(
//                    product = product,
//                    quantity = 10,
//                    buyerId = buyerId,
//                    shop = product.shop
//                )
//
//                every { productRepository.findByIdOrNull(any()) } returns product
//
//                every { itemCartRepository.findByProductIdAndBuyerId(any(), any()) } returns itemCart.apply { id = 1L }
//
//                every { itemCartRepository.save(any()) } returns itemCart.apply { quantity += request.quantity }
//
//                Then("상품 개수가 추가된다.") {
//                    itemCartService.addItemIntoCart(product.id!!, request, buyerId)
//                }
//
//            }
//
//        }
//
//        this.Given("장바구니에 담긴 상품의 개수를 변경할 때") {
//
//            val buyerId = 1L
//
//            val shop = Shop(
//                sellerId = 1L,
//                name = "testName",
//                description = "testDescription",
//                shopImage = "testImage",
//                rate = 0.0f
//            ).apply { id = 1L }
//
//            val product = Product(
//                name = "Test Product",
//                description = "Test Description",
//                productImage = "image.jpg",
//                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//                updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
//                isSoldOut = false,
//                deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
//                isDeleted = false,
//                shop = shop,
//                categoryId = 1L
//            ).apply { id = 1L }
//
//            val itemCart: ItemCart = ItemCart(
//                product = product,
//                quantity = 5,
//                buyerId = buyerId,
//                shop = product.shop
//            ).apply { id = 1L }
//
//
//
//            When("변경할 수량이 1개 이상이면") {
//                val request: SelectProductQuantity = SelectProductQuantity(quantity = 5)
//
//                every { productRepository.findByIdOrNull(any()) } returns product
//                every { itemCartRepository.findByProductIdAndBuyerId(any(), any()) } returns itemCart
//
//                Then("수량이 변경된다.") {
//                    every { itemCartRepository.save(any()) } returns itemCart.apply { quantity = request.quantity }
//
//                    val response = itemCartService.updateItemIntoCart(product.id!!, request, buyerId)
//
//                    response.msg shouldBe "수량이 변경됐습니다."
//                }
//
//            }
//
//            When("상품이 장바구니에 없으면") {
//                val request: SelectProductQuantity = SelectProductQuantity(quantity = 5)
//
//                every { productRepository.findByIdOrNull(any()) } returns product
//                every { itemCartRepository.findByProductIdAndBuyerId(any(), any()) } returns null
//
//                Then("상품이 없다는 예외가 발생한다.") {
//                    shouldThrow<ItemNotFoundException> {
//                        itemCartService.updateItemIntoCart(product.id!!, request, buyerId)
//                    }.let {
//                        it.message shouldBe "장바구니에 해당 상품이 존재하지 않습니다."
//                    }
//                }
//            }
//
//        }
//
//        this.Given("장바구니에서 상품을 삭제할 때") {
//            val buyerId = 1L
//
//            val shop = Shop(
//                sellerId = 1L,
//                name = "testName",
//                description = "testDescription",
//                shopImage = "testImage",
//                rate = 0.0f
//            ).apply { id = 1L }
//
//            val product = Product(
//                name = "Test Product",
//                description = "Test Description",
//                productImage = "image.jpg",
//                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//                updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
//                isSoldOut = false,
//                deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
//                isDeleted = false,
//                shop = shop,
//                categoryId = 1L
//            ).apply { id = 1L }
//
//            val itemCart: ItemCart = ItemCart(
//                product = product,
//                quantity = 5,
//                buyerId = buyerId,
//                shop = product.shop
//            ).apply { id = 1L }
//
//            When("상품이 존재하면") {
//                every { itemCartRepository.findByProductIdAndBuyerId(any(), any()) } returns itemCart
//
//                Then("상품이 삭제된다.") {
//                    itemCartService.deleteItemIntoCart(product.id!!, buyerId)
//                }
//            }
//
//            When("상품이 존재하지 않으면") {
//                every { itemCartRepository.findByProductIdAndBuyerId(any(), any()) } returns null
//
//                Then("상품이 없다는 예외가 발생한다.") {
//                    val error = shouldThrow<ItemNotFoundException> {
//                        itemCartService.deleteItemIntoCart(product.id!!, buyerId)
//                    }
//                    error.message shouldBe "장바구니에 해당 상품이 존재하지 않습니다."
//                }
//            }
//        }
//
//        this.Given("장바구니에 상품이 존재하면") {
//            val buyerId = 1L
//
//            // 가게 3개 생성
//            val shop = (1..3).map {
//                Shop(
//                    sellerId = it.toLong(),
//                    name = "testName $it",
//                    description = "testDescription $it",
//                    shopImage = "testImage $it",
//                    rate = it.toFloat()
//                ).apply { id = it.toLong() }
//            }
//
//            // 가게 1번의 상품 3개
//            val product1 = (1..3).map {
//                Product(
//                    name = "Test Product $it",
//                    description = "Test Description $it",
//                    productImage = "image.jpg $it",
//                    createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//                    updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
//                    isSoldOut = false,
//                    deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
//                    isDeleted = false,
//                    shop = shop[0],
//                    categoryId = 1L,
//                ).apply { id = it.toLong() }
//            }
//
//            // 가게 2번의 상품 2개
//            val product2 = (4..5).map {
//                Product(
//                    name = "Test Product $it",
//                    description = "Test Description $it",
//                    productImage = "image.jpg $it",
//                    createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//                    updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
//                    isSoldOut = false,
//                    deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
//                    isDeleted = false,
//                    shop = shop[1],
//                    categoryId = 1L
//                ).apply { id = it.toLong() }
//            }
//
//            // 가게 3번의 상품 1개
//            val product3 = Product(
//                name = "Test Product 6",
//                description = "Test Description 6",
//                productImage = "image.jpg 6",
//                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
//                updatedAt = LocalDateTime.of(2021, 2, 2, 2, 2, 0),
//                isSoldOut = false,
//                deletedAt = LocalDateTime.of(2023, 3, 3, 3, 3, 0),
//                isDeleted = false,
//                shop = shop[2],
//                categoryId = 1L
//            ).apply { id = 6 }
//
//            val productBackOffice1: List<ProductBackOffice> = product1.map {
//                ProductBackOffice(
//                    quantity = it.id!!.toInt() * 15,
//                    price = it.id!!.toInt() * 1000,
//                    soldQuantity = 0,
//                    product = it
//                ).apply { id = it.id }
//            }
//
//            val productBackOffice2: List<ProductBackOffice> = product2.map {
//                ProductBackOffice(
//                    quantity = it.id!!.toInt() * 15,
//                    price = it.id!!.toInt() * 1000,
//                    soldQuantity = 0,
//                    product = it
//                ).apply { id = it.id }
//            }
//
//            val productBackOffice3: ProductBackOffice = ProductBackOffice(
//                quantity = product3.id!!.toInt() * 15,
//                price = product3.id!!.toInt() * 1000,
//                soldQuantity = 0,
//                product = product3
//            ).apply { id = product3.id }
//
//            for (i in 0..2) {
//                product1[i].productBackOffice = productBackOffice1[i]
//            }
//
//            for (i in 0..1) {
//                product2[i].productBackOffice = productBackOffice2[i]
//            }
//            product3.productBackOffice = productBackOffice3
//
//            // 장바구니에 상품 담기
//            val cart: MutableList<ItemCart> = mutableListOf()
//
//            cart.addAll(product1.map {
//                ItemCart(
//                    product = it,
//                    quantity = 5,
//                    buyerId = buyerId,
//                    shop = it.shop
//                ).apply { id = it.id }
//            })
//
//            cart.addAll(product2.map {
//                ItemCart(
//                    product = it,
//                    quantity = 5,
//                    buyerId = buyerId,
//                    shop = it.shop
//                ).apply { id = it.id }
//            })
//
//            cart.add(
//                ItemCart(
//                    product = product3,
//                    quantity = 5,
//                    buyerId = buyerId,
//                    shop = product3.shop
//                ).apply { id = product3.id }
//            )
//
//            When("구매자가 상품을 조회하면") {
//
//                every { itemCartRepository.findByBuyerId(any()) } returns cart
//
//                val result = itemCartService.getMyCart(buyerId)
//
//                Then("가게별로 묶여서 상품이 조회된다.") {
//                    result.size shouldBe 3 // 총 가게 수
//
//                    // 가게 별 상품들
//                    result[0].items.size shouldBe 3
//                    result[0].shopId shouldBe product1[0].shop.id
//                    result[1].items.size shouldBe 2
//                    result[1].shopId shouldBe product2[0].shop.id
//                    result[2].items.size shouldBe 1
//                    result[2].shopId shouldBe product3.shop.id
//
//                    result[2].items[0].cartId shouldBe 6L
//
//                }
//            }
//
//        }
//    }
//}