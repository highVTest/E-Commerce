package com.highv.ecommerce.item_cart

import com.highv.ecommerce.domain.item_cart.dto.request.SelectProductQuantity
import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.item_cart.service.ItemCartService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ItemCartServiceTest : BehaviorSpec() {

    init {

        val itemCartRepository = mockk<ItemCartRepository>(relaxed = true)
        val productRepository = mockk<ProductRepository>(relaxed = true)

        val itemCartService: ItemCartService = ItemCartService(itemCartRepository, productRepository)

        afterContainer {
            clearAllMocks()
        }

        this.Given("상품을 장바구니에 담을 때") {

            val buyerId = 1L

            val product = Product(
                name = "Test Product",
                description = "Test Description",
                price = 1000,
                productImage = "image.jpg",
                favorite = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                quantity = 10,
                isSoldOut = false,
                deletedAt = LocalDateTime.now(),
                isDeleted = false,
                shopId = 1L,
                categoryId = 1L
            ).apply { id = 1L }


            When("상품 수량이 1개 이상이면") {
                val request: SelectProductQuantity = SelectProductQuantity(quantity = 1)

                every { productRepository.findByIdOrNull(any()) } returns product

                every { itemCartRepository.save(any()) } returns ItemCart(
                    productId = 1L,
                    productName = product.name,
                    price = 3000 * request.quantity, // 추후 프로덕트에서 price 관련된 게 생길 예정
                    quantity = request.quantity,
                    buyerId = buyerId
                ).apply { id = buyerId }

                Then("장바구니에 추가된다.") {
                    itemCartService.addItemIntoCart(product.id!!, request, buyerId)

                    verify(exactly = 1) { productRepository.findByIdOrNull(any()) }
                }

            }

            When("상품 수량이 1개보다 적으면") {
                val request: SelectProductQuantity = SelectProductQuantity(quantity = 0)

                every { productRepository.findByIdOrNull(any()) } returns product

                Then("상품 개수 예외가 발생한다.") {
                    shouldThrow<RuntimeException> {
                        itemCartService.addItemIntoCart(product.id!!, request, buyerId)
                    }.let {
                        it.message shouldBe "상품의 개수가 1개보다 적을 수 없습니다."
                    }
                }
            }

            When("존재하지 않는 상품일 때") {
                val request: SelectProductQuantity = SelectProductQuantity(quantity = 1)
                every { productRepository.findByIdOrNull(any()) } returns null

                Then("상품이 없다는 예외가 발생한다.") {
                    shouldThrow<RuntimeException> { itemCartService.addItemIntoCart(1L, request, buyerId) }
                        .let {
                            it.message shouldBe "Product not found"
                        }
                }
            }

        }

        this.Given("장바구니에 담긴 상품의 개수를 변경할 때") {

            val buyerId = 1L

            val product = Product(
                name = "Test Product",
                description = "Test Description",
                price = 1000,
                productImage = "image.jpg",
                favorite = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                quantity = 10,
                isSoldOut = false,
                deletedAt = LocalDateTime.now(),
                isDeleted = false,
                shopId = 1L,
                categoryId = 1L
            ).apply { id = 1L }

            val itemCart: ItemCart = ItemCart(
                productId = 1L,
                productName = product.name,
                price = 3000,
                quantity = 5,
                buyerId = buyerId
            ).apply { id = 1L }



            When("변경할 수량이 1개 이상이면") {
                val request: SelectProductQuantity = SelectProductQuantity(quantity = 5)

                every { productRepository.findByIdOrNull(any()) } returns product
                every { itemCartRepository.findByProductIdAndBuyerIdAndIsDeletedFalse(any(), any()) } returns itemCart

                Then("수량과 총액이 변경된다.") {
                    every { itemCartRepository.save(any()) } returns itemCart.apply { 3000 * request.quantity }

                    itemCartService.updateItemIntoCart(product.id!!, request, buyerId)
                }

            }

            When("상품이 장바구니에 없으면") {
                val request: SelectProductQuantity = SelectProductQuantity(quantity = 5)

                every { productRepository.findByIdOrNull(any()) } returns product
                every { itemCartRepository.findByProductIdAndBuyerIdAndIsDeletedFalse(any(), any()) } returns null

                Then("상품이 없다는 예외가 발생한다.") {
                    shouldThrow<RuntimeException> {
                        itemCartService.updateItemIntoCart(product.id!!, request, buyerId)
                    }.let {
                        it.message shouldBe "Item not found"
                    }
                }
            }

        }
    }
}