package com.highv.ecommerce.product

import com.highv.ecommerce.common.lock.service.RedisLockService
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeRequest
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.product.service.ProductService
import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ProductServiceTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val shopRepository = mockk<ShopRepository>()
    val sellerRepository = mockk<SellerRepository>()
    val productBackOfficeRepository = mockk<ProductBackOfficeRepository>()
    val favoriteRepository = mockk<FavoriteRepository>()
    val buyerRepository = mockk<BuyerRepository>()
    val redisLockService = mockk<RedisLockService>()

    val favoriteService = FavoriteService(favoriteRepository, productRepository, buyerRepository)
    val productService = ProductService(
        productRepository,
        shopRepository,
        sellerRepository,
        productBackOfficeRepository,
        favoriteService,
        redisLockService
    )

    afterEach {
        clearAllMocks()
    }

    Given("createProduct 실행") {

        val productRequest = CreateProductRequest(
            name = "Product Name",
            description = "Product Description",
            categoryId = 1L,
            imageUrl = ""
        )
        val productBackOfficeRequest = ProductBackOfficeRequest(
            quantity = 10,
            price = 100,
        )

        every { redisLockService.runExclusiveWithRedissonLock(any(), any(), any<() -> Any>()) } answers {
            thirdArg<() -> Any>().invoke()
        }

        every { sellerRepository.findByIdOrNull(seller.id) } returns seller
        every { shopRepository.findShopBySellerId(seller.id!!) } returns shop

        every { productRepository.existsByNameAndShopId(productRequest.name, shop.id!!) } returns false
        every { productRepository.save(any()) } answers { firstArg() }

        every { productRepository.save(any()) } answers {
            val savedProduct = firstArg<Product>()
            savedProduct.apply { id = 1L }
        }

        every { productBackOfficeRepository.save(any()) } answers {
            val productBackOffice = firstArg<ProductBackOffice>()
            productBackOffice.apply { id = 1L }
        }

        When("해당 메서드를 호출했을 때") {
            val result = productService.createProduct(
                sellerId = seller.id!!,
                productRequest = productRequest,
                productBackOfficeRequest = productBackOfficeRequest
            )

            Then("추가한 상품 정보를 반환한다") {
                result.name shouldBe productRequest.name
                verify(exactly = 2) { productRepository.save(any()) }
                verify(exactly = 1) { productBackOfficeRepository.save(any()) }
            }
        }
    }

    Given("updateProduct 실행") {

        val product = Product(
            name = "Product Name",
            description = "Product Description",
            productImage = "",
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
            quantity = 10,
            price = 100,
            soldQuantity = 0,
            product = product
        )

        product.productBackOffice = productBackOffice

        val updateProductRequest = UpdateProductRequest(
            name = "Updated Product Name",
            description = "Updated Product Description",
            isSoldOut = true,
            categoryId = 2L
        )

        val updatedProduct = product.apply {
            name = updateProductRequest.name
            description = updateProductRequest.description
            isSoldOut = updateProductRequest.isSoldOut
            categoryId = updateProductRequest.categoryId
            updatedAt = LocalDateTime.now()
        }

        // Mock the repository methods
        every { productRepository.findByIdOrNull(product.id!!) } returns product
        every { productRepository.save(any()) } answers {
            val savedProduct = firstArg<Product>()
            savedProduct.apply { updatedAt = LocalDateTime.now() }
        }
        every { productBackOfficeRepository.save(any()) } answers {
            val savedProductBO = firstArg<ProductBackOffice>()
            savedProductBO.apply { id = 1L }
        }
        every { favoriteRepository.countFavoriteByProductId(product.id!!) } returns 0

        When("해당 메서드를 호출했을 때") {
            val result = productService.updateProduct(
                sellerId = seller.id!!,
                productId = product.id!!,
                updateProductRequest = updateProductRequest
            )

            Then("업데이트된 상품 정보를 반환한다") {
                result.name shouldBe updateProductRequest.name
                result.isSoldOut shouldBe updateProductRequest.isSoldOut

                verify(exactly = 1) { productRepository.findByIdOrNull(product.id!!) }
                verify(exactly = 1) { productRepository.save(updatedProduct) }
                verify(exactly = 1) { favoriteRepository.countFavoriteByProductId(product.id!!) }
            }
        }
    }

    Given("deleteProduct 실행") {

        val product = Product(
            name = "Product Name",
            description = "Product Description",
            productImage = "",
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
            quantity = 10,
            price = 100,
            soldQuantity = 0,
            product = product
        )

        product.productBackOffice = productBackOffice

        every { productRepository.findByIdOrNull(product.id!!) } returns product
        every { productRepository.save(any()) } answers {
            val updatedProduct = firstArg<Product>()
            updatedProduct.apply { id = product.id!! }
        }

        When("해당 메서드를 호출했을 때") {
            productService.deleteProduct(seller.id!!, product.id!!)

            Then("상품이 삭제 상태로 변경된다") {
                verify(exactly = 1) { productRepository.findByIdOrNull(product.id!!) }
                verify(exactly = 1) { productRepository.save(any()) }
            }
        }
    }

    Given("getProductById 실행") {
        val product = Product(
            name = "Product Name",
            description = "Product Description",
            productImage = "",
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
            quantity = 10,
            price = 100,
            soldQuantity = 0,
            product = product
        )

        product.productBackOffice = productBackOffice

        every { productRepository.findByIdOrNull(product.id!!) } returns product
        every { favoriteRepository.countFavoriteByProductId(product.id!!) } returns 0

        When("해당 메서드를 호출했을 때") {
            val result = productService.getProductById(product.id!!)

            Then("상품 정보를 반환한다") {
                result.name shouldBe product.name
                result.productImage shouldBe product.productImage

                verify(exactly = 1) { productRepository.findByIdOrNull(product.id!!) }
                verify(exactly = 1) { favoriteRepository.countFavoriteByProductId(product.id!!) }
            }
        }
    }

    Given("getProductsByCategory 실행") {
        val categoryId = 1L
        val pageable1 = Pageable.ofSize(2).withPage(0)
        val products = (1L..5L).map { id ->
            val product = Product(
                name = "P$id",
                description = "Description",
                productImage = "",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isSoldOut = false,
                deletedAt = null,
                isDeleted = false,
                shop = shop,
                categoryId = categoryId,
                productBackOffice = null
            ).apply { this.id = id }

            val productBackOffice = ProductBackOffice(
                id = id,
                quantity = 10,
                price = 100,
                soldQuantity = 0,
                product = product
            )
            product.productBackOffice = productBackOffice
            product
        }

        val page1 = PageImpl(products.subList(0, 2), pageable1, products.size.toLong())

        every {
            productRepository.findByCategoryPaginated(
                categoryId,
                match { pageable ->
                    pageable.pageNumber == 0 && pageable.pageSize == 2 && pageable.sort.isUnsorted
                }
            )
        } returns page1
        products.forEach { product ->
            every { favoriteService.countFavorite(product.id!!) } returns 0
        }

        When("첫 번째 페이지를 호출했을 때") {
            val result = productService.getProductsByCategory(categoryId, pageable1)

            Then("첫 번째 페이지의 3개의 상품을 반환한다") {
                result.content.size shouldBe 2
                result.content[0].name shouldBe "P1"
                result.content[1].name shouldBe "P2"

                verify(exactly = 1) {
                    productRepository.findByCategoryPaginated(
                        categoryId,
                        match { pageable ->
                            pageable.pageNumber == 0 && pageable.pageSize == 2 && pageable.sort.isUnsorted
                        }
                    )
                }
            }
        }
    }
}) {
    companion object {
        private val seller = Seller(
            id = 1L,
            nickname = "S1",
            password = "12345678",
            email = "S1@test.com",
            profileImage = "",
            phoneNumber = "010-1234-1234",
            address = "addr",
            activeStatus = ActiveStatus.APPROVED
        )

        private val shop = Shop(
            sellerId = 1L,
            name = "ShopName",
            description = "ShopDescription",
            rate = 5.0f,
            shopImage = "Image"
        ).apply { id = 1L }
    }
}