package com.highv.ecommerce

import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.product.service.ProductService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ProductServiceTest : BehaviorSpec({

    val productRepository = mockk<ProductRepository>(relaxed = true)
    val productService = ProductService(productRepository)

    given("a new product creation request") {
        val request = CreateProductRequest(
            name = "Test Product",
            description = "Test Description",
            price = 1000,
            productImage = "image.jpg",
            createdAt = LocalDateTime.now(),
            quantity = 10,
            shopId = 1L,
            categoryId = 1L
        )
        val product = Product(
            name = "Test Product",
            description = "Test Description",
            productImage = "image.jpg",
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shopId = 1L,
            categoryId = 1L
        ).apply { id = 1L }

        every { productRepository.save(any<Product>()) } returns product

        `when`("the product is created") {
            val response = productService.createProduct(request)

            then("the product should be saved and returned correctly") {
                response.name shouldBe "Test Product"
                response.description shouldBe "Test Description"
//                response.price shouldBe 1000

                verify { productRepository.save(any<Product>()) }
            }
        }
    }

    given("an existing product update request") {
        val product = Product(
            name = "Test Product",
            description = "Test Description",
            productImage = "image.jpg",
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shopId = 1L,
            categoryId = 1L
        ).apply { id = 1L }

        val updateRequest = UpdateProductRequest(
            name = "Updated Product",
            description = "Updated Description",
            price = 2000,
            productImage = "updated_image.jpg",
            updatedAt = LocalDateTime.now(),
            quantity = 20,
            isSoldOut = false,
            categoryId = 2L
        )

        every { productRepository.findByIdOrNull(1L) } returns product
        every { productRepository.save(any<Product>()) } returns product.apply {
            name = updateRequest.name
            description = updateRequest.description
            productImage = updateRequest.productImage
            isSoldOut = updateRequest.isSoldOut
            updatedAt = updateRequest.updatedAt
            categoryId = updateRequest.categoryId
        }

        `when`("the product is updated") {
            val response = productService.updateProduct(1L, updateRequest)

            then("the product should be updated and returned correctly") {
                response.name shouldBe "Updated Product"
                response.description shouldBe "Updated Description"
//                response.price shouldBe 2000

                verify { productRepository.findByIdOrNull(1L) }
                verify { productRepository.save(any<Product>()) }
            }
        }
    }

    given("an update request for a non-existing product") {
        every { productRepository.findByIdOrNull(1L) } returns null

        `when`("the update is attempted") {
            val exception = shouldThrow<RuntimeException> {
                productService.updateProduct(1L, UpdateProductRequest(
                    name = "Non-existing Product",
                    description = "Non-existing Description",
                    price = 2000,
                    productImage = "non_existing_image.jpg",
                    updatedAt = LocalDateTime.now(),
                    quantity = 20,
                    isSoldOut = false,
                    categoryId = 2L
                ))
            }

            then("an exception should be thrown") {
                exception.message shouldBe "Product not found"
                verify { productRepository.findByIdOrNull(1L) }
            }
        }
    }

    given("a product deletion request") {
        val product = Product(
            name = "Test Product",
            description = "Test Description",
//            price = 1000,
            productImage = "image.jpg",
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
//            quantity = 10,
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shopId = 1L,
            categoryId = 1L
        ).apply { id = 1L }

        every { productRepository.findByIdOrNull(1L) } returns product
        every { productRepository.save(any<Product>()) } returns product.apply {
            isDeleted = true
            deletedAt = LocalDateTime.now()
        }

        `when`("the product is deleted") {
            productService.deleteProduct(1L)

            then("the product should be marked as deleted") {
                verify { productRepository.findByIdOrNull(1L) }
                verify { productRepository.save(any<Product>()) }
            }
        }
    }

    given("a product retrieval request by ID") {
        val product = Product(
            name = "Test Product",
            description = "Test Description",
//            price = 1000,
            productImage = "image.jpg",
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
//            quantity = 10,
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shopId = 1L,
            categoryId = 1L
        ).apply { id = 1L }

        every { productRepository.findByIdOrNull(1L) } returns product

        `when`("the product is retrieved") {
            val response = productService.getProductById(1L)

            then("the correct product should be returned") {
                response.name shouldBe "Test Product"
                response.description shouldBe "Test Description"
//                response.price shouldBe 1000

                verify { productRepository.findByIdOrNull(1L) }
            }
        }

        `when`("a non-existing product is retrieved") {
            every { productRepository.findByIdOrNull(2L) } returns null

            val exception = shouldThrow<RuntimeException> {
                productService.getProductById(2L)
            }

            then("an exception should be thrown") {
                exception.message shouldBe "Product not found"
                verify { productRepository.findByIdOrNull(2L) }
            }
        }
    }
})
