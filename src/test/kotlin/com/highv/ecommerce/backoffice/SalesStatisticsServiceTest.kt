package com.highv.ecommerce.backoffice

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.backoffice.service.SalesStatisticsService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class SalesStatisticsServiceTest : BehaviorSpec({

    val productRepository = mockk<ProductRepository>()
    val productBackOfficeRepository = mockk<ProductBackOfficeRepository>()
    val shopRepository = mockk<ShopRepository>()
    val salesStatisticsService = SalesStatisticsService(productBackOfficeRepository, productRepository, shopRepository)

    afterEach {
        clearAllMocks()
    }

    Given("validateProduct 실행") {
        val product1 = Product(
            name = "ProductName1",
            description = "Product Description 1",
            productImage = "Image1",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L
        ).apply { id = 1L }

        val productBackOffice1 = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product1
        )
        product1.productBackOffice = productBackOffice1

        val product2 = Product(
            name = "ProductName2",
            description = "Product Description 2",
            productImage = "Image2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 2L
        ).apply { id = 2L }

        val productBackOffice2 = ProductBackOffice(
            id = 2L,
            quantity = 200,
            price = 3000,
            soldQuantity = 10,
            product = product2
        )
        product2.productBackOffice = productBackOffice2

        val products = listOf(product1, product2)

        When("정상적인 경우") {
            every { shopRepository.findByIdOrNull(seller.id) } returns shop
            every { productRepository.findAllByShopId(shop.id!!) } returns products

            val result = salesStatisticsService.validateProduct(shop.id!!)
            Then("제품 목록을 반환한다.") {
                result shouldBe products
            }
        }

        When("상점이 없을 경우") {
            every { shopRepository.findByIdOrNull(seller.id) } returns null
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    salesStatisticsService.validateProduct(shop.id!!)
                }
            }
        }
    }

    Given("getTotalSales 실행") {
        val product1 = Product(
            name = "ProductName1",
            description = "Product Description 1",
            productImage = "Image1",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L
        ).apply { id = 1L }
        val productBackOffice1 = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product1
        )
        product1.productBackOffice = productBackOffice1

        val product2 = Product(
            name = "ProductName2",
            description = "Product Description 2",
            productImage = "Image2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 2L
        ).apply { id = 2L }
        val productBackOffice2 = ProductBackOffice(
            id = 2L,
            quantity = 200,
            price = 3000,
            soldQuantity = 10,
            product = product2
        )
        product2.productBackOffice = productBackOffice2

        val products = listOf(product1, product2)

        val totalSalesResponse = TotalSalesResponse(
            totalQuantity = (productBackOffice1.soldQuantity + productBackOffice2.soldQuantity).toInt(),
            totalPrice = ((productBackOffice1.soldQuantity * productBackOffice1.price) + (productBackOffice2.soldQuantity * productBackOffice2.price)).toInt()
        )

        When("정상적인 경우") {
            every { shopRepository.findByIdOrNull(seller.id) } returns shop
            every { productRepository.findAllByShopId(shop.id!!) } returns products
            every {
                productBackOfficeRepository.findTotalSalesStatisticsByProductIds(
                    listOf(
                        1L,
                        2L
                    )
                )
            } returns totalSalesResponse

            val result = salesStatisticsService.getTotalSales(shop.id!!)
            Then("총 판매량과 판매 금액을 반환한다.") {
                result shouldBe totalSalesResponse
            }
        }
    }

    Given("getProductSales 실행") {
        val product1 = Product(
            name = "ProductName1",
            description = "Product Description 1",
            productImage = "Image1",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L
        ).apply { id = 1L }
        val productBackOffice1 = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 10, // Example sold quantity
            product = product1
        )
        product1.productBackOffice = productBackOffice1

        val product2 = Product(
            name = "ProductName2",
            description = "Product Description 2",
            productImage = "Image2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 2L
        ).apply { id = 2L }
        val productBackOffice2 = ProductBackOffice(
            id = 2L,
            quantity = 200,
            price = 3000,
            soldQuantity = 20, // Example sold quantity
            product = product2
        )
        product2.productBackOffice = productBackOffice2

        val products = listOf(product1, product2)

        val expectedResponses = listOf(
            ProductSalesResponse(
                productName = product1.name,
                productQuantity = productBackOffice1.soldQuantity,
                productPrice = productBackOffice1.soldQuantity * productBackOffice1.price
            ),
            ProductSalesResponse(
                productName = product2.name,
                productQuantity = productBackOffice2.soldQuantity,
                productPrice = productBackOffice2.soldQuantity * productBackOffice2.price
            )
        )

        When("정상적인 경우") {
            every { shopRepository.findByIdOrNull(seller.id) } returns shop
            every { productRepository.findAllByShopId(shop.id!!) } returns products

            val result = salesStatisticsService.getProductSales(shop.id!!)
            Then("제품별 판매량과 판매 금액을 반환한다.") {
                result shouldBe expectedResponses
            }
        }

        When("backoffice가 없는 경우") {
            product1.productBackOffice = null

            every { shopRepository.findByIdOrNull(seller.id) } returns shop
            every { productRepository.findAllByShopId(shop.id!!) } returns products

            Then("CustomRuntimeException을 던진다") {
                shouldThrow<CustomRuntimeException> {
                    salesStatisticsService.getProductSales(shop.id!!)
                }
            }
        }
    }

}) {
    companion object {
        private val shop = Shop(
            sellerId = 1L,
            name = "name",
            description = "description",
            shopImage = "shopImage",
            rate = 1f
        ).apply { id = 1L }

        private val seller = Seller(
            id = 1L,
            nickname = "HR",
            password = "123456789",
            email = "HR@test.com",
            profileImage = "profileImage",
            phoneNumber = "010-1234-5678",
            address = "주소입니다",
            activeStatus = ActiveStatus.APPROVED
        )
    }
}