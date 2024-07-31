package com.highv.ecommerce.backoffice

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.backoffice.service.SalesStatisticsService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

class SalesStatisticsServiceTest : BehaviorSpec({

    val productRepository = mockk<ProductRepository>()
    val productBackOfficeRepository = mockk<ProductBackOfficeRepository>()
    val salesStatisticsService = SalesStatisticsService(productBackOfficeRepository, productRepository)

    Given("getTotalSalesQuantity 를 실행 시킬 때") {
        When("price가 10000원이고 soldQuantity 가 20를 판매한 상품과 20000원이고 2개 를 판 상품이 있을 경우") {
            val sellerId = 1L
            val products: List<Long> = listOf(1, 2)
            Then("22 를 리턴 한다") {

                every { productRepository.findAllByShopId(sellerId) } returns listOf(product1, product2)
                every {
                    productBackOfficeRepository.findTotalSoldQuantitiesByProductIds(products)
                } returns 22

                val result = salesStatisticsService.getTotalSalesQuantity(sellerId)

                result.totalQuantity shouldBe 22
            }
        }
        When("productIds 가 비어있을 경우") {
            val sellerId = 1L
            Then("0 을 리턴 한다") {

                every { productRepository.findAllByShopId(sellerId) } returns listOf()

                val result = salesStatisticsService.getTotalSalesQuantity(sellerId)

                result.totalQuantity shouldBe 0
            }
        }
    }

    Given("getTotalSalesAmount 를 실행 시킬 때") {
        When("price가 10000원이고 soldQuantity 가 20를 판매한 상품과 20000원이고 2개 를 판 상품이 있을 경우") {
            val sellerId = 1L
            val products: List<Long> = listOf(1, 2)
            Then("240000 를 리턴 한다") {

                every { productRepository.findAllByShopId(sellerId) } returns listOf(product1, product2)
                every {
                    productBackOfficeRepository.findTotalSalesAmountByProductIds(products)
                } returns 240000

                val result = salesStatisticsService.getTotalSalesAmount(sellerId)

                result.totalPrice shouldBe 240000
            }
        }
        When("productIds 가 비어있을 경우") {
            val sellerId = 1L
            Then("0 을 리턴 한다") {

                every { productRepository.findAllByShopId(sellerId) } returns listOf()

                val result = salesStatisticsService.getTotalSalesAmount(sellerId)

                result.totalPrice shouldBe 0
            }
        }
    }

    Given("getProductSalesQuantity 를 실행 시킬 때") {
        // validateProductWithBackOffice Throw 검증
        When("validateProductWithBackOffice 에서 productId 가 없을 경우") {
            val productId = 1L
            every { productRepository.findByIdOrNull(productId) } returns null
            Then("IllegalArgumentException 를 Throw 한다") {
                shouldThrow<IllegalArgumentException> {
                    salesStatisticsService.getProductSalesQuantity(1L, productId)
                }.let {
                    it.message shouldBe "Product with ID $productId not found"
                }
            }

        }

        // validateProductWithBackOffice Throw 검증
        When("validateProductWithBackOffice 에서 shop의 sellerid와 sellerId가 다를 경우") {
            product1.shop.sellerId = 1L
            val sellerId = 3L
            every { productRepository.findByIdOrNull(1L) } returns product1
            Then("IllegalArgumentException 를 Throw 한다") {
                shouldThrow<IllegalArgumentException> {
                    salesStatisticsService.getProductSalesQuantity(sellerId, 1L)
                }.let {
                    it.message shouldBe "No Authority"
                }
            }

        }

        When("productBackOffice 가 없을 경우") {
            val sellerId = 1L
            val productId = 1L
            product1.productBackOffice = null
            Then("IllegalArgumentException 을 Throw 한다") {
                every { productRepository.findByIdOrNull(any()) } returns product1

                shouldThrow<IllegalArgumentException> {
                    salesStatisticsService.getProductSalesQuantity(sellerId, productId)
                }.let {
                    it.message shouldBe "ProductBackOffice not found for product with ID $productId"
                }
            }
        }
        When("product1 이 20개의 상품을 팔았을 경우") {
            val sellerId = 1L
            val product = 1L
            Then("productSalesQuantity 가 20 이고 productName 은 name 을 리턴 한다") {

                product1.productBackOffice = productBackOffice1

                every { productRepository.findByIdOrNull(product) } returns product1

                val result = salesStatisticsService.getProductSalesQuantity(sellerId, product)

                result.productName shouldBe "name"
                result.productSalesQuantity shouldBe 20
            }
        }
    }

    Given("getProductSales 를 실행 시킬 때") {
        When("productBackOffice 가 없을 경우") {
            val sellerId = 1L
            val productId = 1L
            product1.productBackOffice = null
            Then("IllegalArgumentException 을 Throw 한다") {
                every { productRepository.findByIdOrNull(any()) } returns product1

                shouldThrow<IllegalArgumentException> {
                    salesStatisticsService.getProductSales(sellerId, productId)
                }.let {
                    it.message shouldBe "ProductBackOffice not found for product with ID $productId"
                }
            }
        }
        When("product1 이 20개의 상품을 팔았을 경우") {
            val sellerId = 1L
            val product = 1L
            Then("productPrice 가 20000 이고 productName 은 name 을 리턴 한다") {

                product1.productBackOffice = productBackOffice1

                every { productRepository.findByIdOrNull(product) } returns product1

                val result = salesStatisticsService.getProductSales(sellerId, product)

                result.productName shouldBe "name"
                result.productPrice shouldBe 200000
            }
        }
    }
    afterEach {
        clearAllMocks()
    }
}) {
    companion object {

        private val shop = Shop(
            sellerId = 1L,
            name = "name",
            description = "description",
            shopImage = "shopImage",
            rate = 1f
        )

        private val product1 = Product(
            name = "name",
            description = "description",
            productImage = "image",
            createdAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
        ).apply { id = 1L }

        private val product2 = Product(
            name = "name2",
            description = "description2",
            productImage = "image2",
            createdAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            updatedAt = LocalDateTime.of(2021, 1, 1, 1, 0),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
        ).apply { id = 2L }

        private val productBackOffice1 = ProductBackOffice(
            id = 1L,
            quantity = 100,
            product = product1,
            price = 10000,
            soldQuantity = 20
        )
    }
}