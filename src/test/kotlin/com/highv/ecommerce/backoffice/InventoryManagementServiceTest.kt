package com.highv.ecommerce.backoffice

import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.PriceRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.QuantityRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.SellersProductResponse
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.backoffice.service.InventoryManagementService
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
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

class InventoryManagementServiceTest : BehaviorSpec({
    val productBackOfficeRepository = mockk<ProductBackOfficeRepository>()
    val productRepository = mockk<ProductRepository>()
    val shopRepository = mockk<ShopRepository>()

    val inventoryManagementService =
        InventoryManagementService(productBackOfficeRepository, productRepository, shopRepository)

    afterEach {
        clearAllMocks()
    }

    Given("validateProductBO 실행") {
        val product = Product(
            name = "ProductName",
            description = "Product Description",
            productImage = "Image",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
        ).apply { id = 1L }

        val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product
        )
        product.productBackOffice = productBackOffice


        When("정상적인 경우") {
            every { productRepository.findByIdOrNull(product.id!!) } returns product
            Then("올바른 ProductBackOffice를 반환한다") {
                val result = inventoryManagementService.validateProductBO(seller.id!!, product.id!!)
                result shouldBe productBackOffice
            }
        }

        When("ProductBackOffice가 없는 경우") {
            every { productRepository.findByIdOrNull(product.id!!) } returns product
            product.productBackOffice = null

            Then("예외를 발생시킨다") {
                shouldThrow<IllegalArgumentException> {
                    inventoryManagementService.validateProductBO(seller.id!!, product.id!!)
                }.message shouldBe "No ProductBackOffice"
            }
        }

        When("제품이 존재하지 않을 때") {
            every { productRepository.findByIdOrNull(product.id!!) } returns null

            Then("예외를 발생시킨다") {
                shouldThrow<IllegalArgumentException> {
                    inventoryManagementService.validateProductBO(seller.id!!, product.id!!)
                }.message shouldBe "Product with id ${product.id} not found"
            }
        }

        When("판매자가 권한이 없을 때") {
            every { productRepository.findByIdOrNull(product.id!!) } returns product
            product.shop.sellerId = seller.id!! + 1

            Then("예외를 발생시킨다") {
                shouldThrow<IllegalArgumentException> {
                    inventoryManagementService.validateProductBO(seller.id!!, product.id!!)
                }.message shouldBe "No Authority"
            }
        }
    }

    Given("changeQuantity 실행") {
        val product = Product(
            name = "ProductName",
            description = "Product Description",
            productImage = "Image",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
        ).apply { id = 1L }

        val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product
        )

        product.productBackOffice = productBackOffice
        product.shop.sellerId = seller.id!!

        val newQuantity = QuantityRequest(quantity = 150)

        every { productRepository.findByIdOrNull(product.id!!) } returns product
        productBackOffice.quantity = newQuantity.quantity
        every { productBackOfficeRepository.save(any()) } returns productBackOffice

        When("유효한 sellerId와 productId, quantity 를 제공하면") {
            val result = inventoryManagementService.changeQuantity(
                seller.id!!,
                product.id!!,
                QuantityRequest(newQuantity.quantity)
            )

            Then("수량이 변경된 상품을 반환한다") {
                result.quantity shouldBe newQuantity.quantity
                verify(exactly = 1) { productBackOfficeRepository.save(productBackOffice) }
            }
        }
    }
    Given("changePrice 실행") {
        val product = Product(
            name = "ProductName",
            description = "Product Description",
            productImage = "Image",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
        ).apply { id = 1L }

        val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product
        )
        product.productBackOffice = productBackOffice
        product.shop.sellerId = seller.id!!

        val newPrice = PriceRequest(price = 25000)

        every { productRepository.findByIdOrNull(product.id!!) } returns product
        productBackOffice.price = newPrice.price
        every { productBackOfficeRepository.save(any()) } returns productBackOffice

        When("유효한 sellerId와 productId, quantity 를 제공하면") {
            val result = inventoryManagementService.changeQuantity(
                seller.id!!,
                product.id!!,
                QuantityRequest(newPrice.price)
            )

            Then("수량이 변경된 상품을 반환한다") {
                result.quantity shouldBe newPrice.price
                verify(exactly = 1) { productBackOfficeRepository.save(productBackOffice) }
            }
        }
    }

    Given("getSellerProducts 실행") {
        val product1 = Product(
            name = "ProductName1",
            description = "Product Description",
            productImage = "Image",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
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
            description = "Product Description",
            productImage = "Image",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId = 1L,
            productBackOffice = null
        ).apply { id = 2L }

        val productBackOffice2 = ProductBackOffice(
            id = 2L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product2
        )
        product2.productBackOffice = productBackOffice2

        val pageable: Pageable = PageRequest.of(0, 10)
        val products = listOf(product1, product2)
        val productPage = PageImpl(products, pageable, products.size.toLong())

        every { shopRepository.findShopBySellerId(seller.id!!) } returns shop
        every { productRepository.findPaginatedByShopId(shop.id!!, pageable) } returns productPage

        // Define expected responses
        val expectedResponses = products.map {
            SellersProductResponse(
                id = it.id!!,
                name = it.name,
                quantity = it.productBackOffice?.quantity ?: 0,
                price = it.productBackOffice?.price ?: 0,
                createdAt = it.createdAt,
                productImage = it.productImage
            )
        }

        When("유효한 sellerId와 pageable을 제공하면") {
            val result = inventoryManagementService.getSellerProducts(seller.id!!, pageable)

            Then("제품 목록을 반환한다.") {
                result.content.size shouldBe expectedResponses.size
                result.content.zip(expectedResponses).forEach { (actual, expected) ->
                    actual.id shouldBe expected.id
                    actual.name shouldBe expected.name
                    actual.quantity shouldBe expected.quantity
                    actual.price shouldBe expected.price
                    actual.createdAt shouldBe expected.createdAt
                    actual.productImage shouldBe expected.productImage
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