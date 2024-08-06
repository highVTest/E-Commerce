package com.highv.ecommerce.backoffice

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.backoffice.service.InventoryManagementService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class InventoryManagementServiceTest : BehaviorSpec({
    val productBackOfficeRepository = mockk<ProductBackOfficeRepository>()
    val productRepository = mockk<ProductRepository>()
    val shopRepository = mockk<ShopRepository>()

    val inventoryManagementService = InventoryManagementService(productBackOfficeRepository, productRepository, shopRepository)

    Given("getProductsQuantity 메서드를 호출할 때") {
        val sellerId = 1L
        val productId = 1L
        val shop = Shop(sellerId = sellerId, name = "ShopName",description = "ShopDescription", rate = 5.0f, shopImage = "Image")
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
            categoryId=1L,
            productBackOffice = null
            ).apply{id = 1L}

        val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product
        )
        product.productBackOffice = productBackOffice

        every { productRepository.findByIdOrNull(productId) } returns product
        every { productBackOfficeRepository.findProductBackOfficesByProductId(productId) } returns productBackOffice

        When("유효한 sellerId와 productId를 제공하면") {
            val result = inventoryManagementService.getProductsQuantity(sellerId, productId)

            Then("올바른 수량과 가격을 반환한다") {
                result.quantity shouldBe 100
                result.price shouldBe 2000
            }
        }
    }

//    Given("changeQuantity 메서드를 호출할 때") {
//        val sellerId = 1L
//        val productId = 1L
//        val newQuantity = 150
//        val shop = Shop(sellerId = sellerId, name = "ShopName",description = "ShopDescription", rate = 5.0f, shopImage = "Image")
//        val product = Product(
//            name = "ProductName",
//            description="Product Description",
//            productImage="Image",
//            createdAt= LocalDateTime.now(),
//            updatedAt= LocalDateTime.now(),
//            isSoldOut=false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId=1L,
//            productBackOffice = null
//        ).apply{id = 1L}
//        val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 100,
//            price = 2000,
//            soldQuantity = 0,
//            product = product
//        )
//        val updatedProductBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = newQuantity,
//            price = 2000,
//            soldQuantity = 0,
//            product = product
//        )
//        product.productBackOffice = productBackOffice
//
//        every { productRepository.findByIdOrNull(productId) } returns product
//        every { productBackOfficeRepository.findProductBackOfficesByProductId(productId) } returns productBackOffice
//        every { productBackOfficeRepository.save(productBackOffice) } returns updatedProductBackOffice
//
//        When("유효한 sellerId와 productId, quantity를 제공하면") {
//            val result = inventoryManagementService.changeQuantity(sellerId, productId, newQuantity)
//
//            Then("수량이 변경된 상품을 반환한다") {
//                result.quantity shouldBe newQuantity
//                result.price shouldBe 2000
//            }
//        }
//    }

    Given("changePrice 메서드를 호출할 때") {
        val sellerId = 1L
        val productId = 1L
        val newPrice = 2500
        val shop = Shop(sellerId = sellerId, name = "ShopName",description = "ShopDescription", rate = 5.0f, shopImage = "Image")
        val product = Product(
            name = "ProductName",
            description="Product Description",
            productImage="Image",
            createdAt= LocalDateTime.now(),
            updatedAt= LocalDateTime.now(),
            isSoldOut=false,
            deletedAt = null,
            isDeleted = false,
            shop = shop,
            categoryId=1L,
            productBackOffice = null
        ).apply{id = 1L}
        val productBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = 2000,
            soldQuantity = 0,
            product = product
        )
        val updatedProductBackOffice = ProductBackOffice(
            id = 1L,
            quantity = 100,
            price = newPrice,
            soldQuantity = 0,
            product = product
        )
        product.productBackOffice = productBackOffice

        every { productRepository.findByIdOrNull(productId) } returns product
        every { productBackOfficeRepository.findProductBackOfficesByProductId(productId) } returns productBackOffice
        every { productBackOfficeRepository.save(productBackOffice) } returns updatedProductBackOffice

//        When("유효한 sellerId와 productId, price를 제공하면") {
//            val result = inventoryManagementService.changePrice(sellerId, productId, newPrice)
//
//            Then("가격이 변경된 상품을 반환한다") {
//                result.quantity shouldBe 100
//                result.price shouldBe newPrice
//            }
//        }
    }

    afterEach {
        clearAllMocks()
    }
})
