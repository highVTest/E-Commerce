//package com.highv.ecommerce.product
//
//import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeRequest
//import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
//import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
//import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
//import com.highv.ecommerce.domain.favorite.repository.FavoriteRepository
//import com.highv.ecommerce.domain.favorite.service.FavoriteService
//import com.highv.ecommerce.domain.product.dto.CreateProductRequest
//import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
//import com.highv.ecommerce.domain.product.entity.Product
//import com.highv.ecommerce.domain.product.repository.ProductRepository
//import com.highv.ecommerce.domain.product.service.ProductService
//import com.highv.ecommerce.domain.seller.shop.entity.Shop
//import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
//import com.highv.ecommerce.infra.s3.S3Manager
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.Runs
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.just
//import io.mockk.mockk
//import io.mockk.verify
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.Pageable
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.web.multipart.MultipartFile
//import java.time.LocalDateTime
//import java.util.Optional
//
//class ProductServiceTest : BehaviorSpec({
//    val productRepository = mockk<ProductRepository>()
//    val shopRepository = mockk<ShopRepository>()
//    val productBackOfficeRepository = mockk<ProductBackOfficeRepository>()
//    val favoriteRepository = mockk<FavoriteRepository>()
//    val buyerRepository = mockk<BuyerRepository>()
//    val s3Manager = mockk<S3Manager>()
//
//    val favoriteService = FavoriteService(favoriteRepository, productRepository, buyerRepository)
//    val productService =
//        ProductService(productRepository, shopRepository, productBackOfficeRepository, favoriteService, s3Manager)
//
//    afterEach {
//        clearAllMocks()
//    }
//
//    Given("createProduct 실행") {
//        val sellerId = 1L
//        val shop = Shop(
//            sellerId = sellerId,
//            name = "Shop Name",
//            description = "Shop Description",
//            rate = 5.0f,
//            shopImage = "shop_image.jpg"
//        ).apply { id = 1L }
//
//        val productRequest = CreateProductRequest(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            categoryId = 1L
//        )
//
//        val product = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOfficeRequest = ProductBackOfficeRequest(
//            quantity = 10,
//            price = 100,
//            product = product
//        )
//
//        val file = mockk<MultipartFile>()
//        val uploadedFileName = "uploaded_file.jpg"
//        val originalFilename = "original_file.jpg"
//
//        every { file.originalFilename } returns originalFilename
//        every { shopRepository.findShopBySellerId(sellerId) } returns shop
//        every { s3Manager.uploadFile(file) } just Runs
//        every { s3Manager.getFile(originalFilename) } returns uploadedFileName
//
//        every { productRepository.save(any()) } answers {
//            val savedProduct = firstArg<Product>()
//            savedProduct.apply { id = 1L }
//        }
//
//        every { productBackOfficeRepository.save(any()) } answers {
//            val productBackOffice = firstArg<ProductBackOffice>()
//            productBackOffice.apply { id = 1L }
//        }
//
//        When("해당 메서드를 호출했을 때") {
//            val result = productService.createProduct(
//                sellerId = sellerId,
//                productRequest = productRequest,
//                productBackOfficeRequest = productBackOfficeRequest,
//                file = file
//            )
//
//            Then("추가한 상품 정보를 반환한다") {
//                result.name shouldBe productRequest.name
//                result.productImage shouldBe uploadedFileName
//
//                verify(exactly = 1) { s3Manager.uploadFile(file) }
//                verify(exactly = 1) { s3Manager.getFile(originalFilename) }
//                verify(exactly = 2) { productRepository.save(any()) }
//                verify(exactly = 1) { productBackOfficeRepository.save(any()) }
//            }
//        }
//    }
//
//    Given("updateProduct 실행") {
//        val sellerId = 1L
//        val productId = 1L
//
//        val product = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product.productBackOffice = productBackOffice
//
//        val updateProductRequest = UpdateProductRequest(
//            name = "Updated Product Name",
//            description = "Updated Product Description",
//            productImage = "updated_image.jpg",
//            isSoldOut = true,
//            categoryId = 2L
//        )
//
//        val updatedProduct = product.apply {
//            name = updateProductRequest.name
//            description = updateProductRequest.description
//            productImage = updateProductRequest.productImage
//            isSoldOut = updateProductRequest.isSoldOut
//            categoryId = updateProductRequest.categoryId
//            updatedAt = LocalDateTime.now()
//        }
//
//        // Mock the repository methods
//        every { productRepository.findByIdOrNull(productId) } returns product
//        every { productRepository.save(any()) } answers {
//            val savedProduct = firstArg<Product>()
//            savedProduct.apply { updatedAt = LocalDateTime.now() }
//        }
//        every { productBackOfficeRepository.save(any()) } answers {
//            val savedProductBO = firstArg<ProductBackOffice>()
//            savedProductBO.apply { id = 1L }
//        }
//        every { favoriteRepository.countFavoriteByProductId(productId) } returns 0
//
//        When("해당 메서드를 호출했을 때") {
//            val result = productService.updateProduct(
//                sellerId = sellerId,
//                productId = productId,
//                updateProductRequest = updateProductRequest
//            )
//
//            Then("업데이트된 상품 정보를 반환한다") {
//                result.name shouldBe updateProductRequest.name
//                result.productImage shouldBe updateProductRequest.productImage
//                result.isSoldOut shouldBe updateProductRequest.isSoldOut
//
//                verify(exactly = 1) { productRepository.findByIdOrNull(productId) }
//                verify(exactly = 1) { productRepository.save(updatedProduct) }
//                verify(exactly = 1) { favoriteRepository.countFavoriteByProductId(productId) }
//            }
//        }
//    }
//
//
//    Given("deleteProduct 실행") {
//        val sellerId = 1L
//        val productId = 1L
//
//        val product = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product.productBackOffice = productBackOffice
//
//        every { productRepository.findByIdOrNull(productId) } returns product
//        every { productRepository.save(any()) } answers {
//            val updatedProduct = firstArg<Product>()
//            updatedProduct.apply { id = productId }
//        }
//
//        When("해당 메서드를 호출했을 때") {
//            productService.deleteProduct(sellerId, productId)
//
//            Then("상품이 삭제 상태로 변경된다") {
//                verify(exactly = 1) { productRepository.findByIdOrNull(productId) }
//                verify(exactly = 1) { productRepository.save(any()) }
//            }
//        }
//    }
//
//    Given("getProductById 실행") {
//        val productId = 1L
//
//        val product = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product.productBackOffice = productBackOffice
//
//        every { productRepository.findByIdOrNull(productId) } returns product
//        every { favoriteRepository.countFavoriteByProductId(productId) } returns 0
//
//        When("해당 메서드를 호출했을 때") {
//            val result = productService.getProductById(productId)
//
//            Then("상품 정보를 반환한다") {
//                result.name shouldBe product.name
//                result.productImage shouldBe product.productImage
//
//                verify(exactly = 1) { productRepository.findByIdOrNull(productId) }
//                verify(exactly = 1) { favoriteRepository.countFavoriteByProductId(productId) }
//            }
//        }
//    }
//
//    Given("getAllProducts 실행") {
//        val pageable = mockk<Pageable>()
//
//        // Mock Pageable methods
//        every { pageable.offset } returns 0L
//        every { pageable.pageSize } returns 5
//        every { pageable.sort } returns mockk()
//        every { pageable.toOptional() } returns Optional.empty()
//
//        val product = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product.productBackOffice = productBackOffice
//
//        val product2 = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice2 = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product2.productBackOffice = productBackOffice2
//
//        val product3 = Product(
//            name = "Product Name 3",
//            description = "Product Description 3",
//            productImage = "image3.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = true,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 3L }
//
//        val productBackOffice3 = ProductBackOffice(
//            id = 3L,
//            quantity = 5,
//            price = 150,
//            soldQuantity = 10,
//            product = product3
//        )
//
//        product3.productBackOffice = productBackOffice3
//
//        val product4 = Product(
//            name = "Product Name 4",
//            description = "Product Description 4",
//            productImage = "image4.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = true,
//            shop = shop,
//            categoryId = 3L,
//            productBackOffice = null
//        ).apply { id = 4L }
//
//        val productBackOffice4 = ProductBackOffice(
//            id = 4L,
//            quantity = 30,
//            price = 250,
//            soldQuantity = 15,
//            product = product4
//        )
//
//        product4.productBackOffice = productBackOffice4
//
//        val product5 = Product(
//            name = "Product Name 5",
//            description = "Product Description 5",
//            productImage = "image5.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 2L,
//            productBackOffice = null
//        ).apply { id = 5L }
//
//        val productBackOffice5 = ProductBackOffice(
//            id = 5L,
//            quantity = 40,
//            price = 300,
//            soldQuantity = 20,
//            product = product5
//        )
//
//        product5.productBackOffice = productBackOffice5
//
//        val product6 = Product(
//            name = "Product Name 6",
//            description = "Product Description 6",
//            productImage = "image6.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 3L,
//            productBackOffice = null
//        ).apply { id = 6L }
//
//        val productBackOffice6 = ProductBackOffice(
//            id = 6L,
//            quantity = 50,
//            price = 350,
//            soldQuantity = 25,
//            product = product6
//        )
//
//        product6.productBackOffice = productBackOffice6
//
//        val products = PageImpl(listOf(product), pageable, 1)
//
//        every { productRepository.findAllPaginated(pageable) } returns products
//        every { favoriteService.countFavorite(product.id!!) } returns 0
//
//        When("해당 메서드를 호출했을 때") {
//            val result = productService.getAllProducts(pageable)
//
//            Then("상품 목록을 반환한다") {
//                result.content.size shouldBe 1
//                result.content[0].name shouldBe product.name
//
//                verify(exactly = 1) { productRepository.findAllPaginated(pageable) }
//            }
//        }
//    }
//
//    Given("getProductsByCategory 실행") {
//        val categoryId = 1L
//        val pageable = mockk<Pageable>()
//
//        // Mock Pageable methods
//        every { pageable.offset } returns 0L
//        every { pageable.pageSize } returns 5
//        every { pageable.sort } returns mockk()
//        every { pageable.toOptional() } returns Optional.empty()
//
//        val product = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product.productBackOffice = productBackOffice
//
//        val product2 = Product(
//            name = "Product Name",
//            description = "Product Description",
//            productImage = "",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 1L }
//
//        val productBackOffice2 = ProductBackOffice(
//            id = 1L,
//            quantity = 10,
//            price = 100,
//            soldQuantity = 0,
//            product = product
//        )
//
//        product2.productBackOffice = productBackOffice2
//
//        val product3 = Product(
//            name = "Product Name 3",
//            description = "Product Description 3",
//            productImage = "image3.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = true,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 1L,
//            productBackOffice = null
//        ).apply { id = 3L }
//
//        val productBackOffice3 = ProductBackOffice(
//            id = 3L,
//            quantity = 5,
//            price = 150,
//            soldQuantity = 10,
//            product = product3
//        )
//
//        product3.productBackOffice = productBackOffice3
//
//        val product4 = Product(
//            name = "Product Name 4",
//            description = "Product Description 4",
//            productImage = "image4.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = true,
//            shop = shop,
//            categoryId = 3L,
//            productBackOffice = null
//        ).apply { id = 4L }
//
//        val productBackOffice4 = ProductBackOffice(
//            id = 4L,
//            quantity = 30,
//            price = 250,
//            soldQuantity = 15,
//            product = product4
//        )
//
//        product4.productBackOffice = productBackOffice4
//
//        val product5 = Product(
//            name = "Product Name 5",
//            description = "Product Description 5",
//            productImage = "image5.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 2L,
//            productBackOffice = null
//        ).apply { id = 5L }
//
//        val productBackOffice5 = ProductBackOffice(
//            id = 5L,
//            quantity = 40,
//            price = 300,
//            soldQuantity = 20,
//            product = product5
//        )
//
//        product5.productBackOffice = productBackOffice5
//
//        val product6 = Product(
//            name = "Product Name 6",
//            description = "Product Description 6",
//            productImage = "image6.jpg",
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            isSoldOut = false,
//            deletedAt = null,
//            isDeleted = false,
//            shop = shop,
//            categoryId = 3L,
//            productBackOffice = null
//        ).apply { id = 6L }
//
//        val productBackOffice6 = ProductBackOffice(
//            id = 6L,
//            quantity = 50,
//            price = 350,
//            soldQuantity = 25,
//            product = product6
//        )
//
//        product6.productBackOffice = productBackOffice6
//
//        val products = PageImpl(listOf(product), pageable, 1)
//        every { productRepository.findByCategoryPaginated(categoryId, pageable) } returns products
//        every { favoriteService.countFavorite(product.id!!) } returns 0
//
//        When("해당 메서드를 호출했을 때") {
//            val result = productService.getProductsByCategory(categoryId, pageable)
//
//            Then("해당 카테고리의 상품 목록을 반환한다") {
//                result.content.size shouldBe 1
//                result.content[0].name shouldBe product.name
//
//                verify(exactly = 1) { productRepository.findByCategoryPaginated(categoryId, pageable) }
//            }
//        }
//    }
//
//}) {
//    companion object {
//        private val shop = Shop(
//            sellerId = 1L,
//            name = "ShopName",
//            description = "ShopDescription",
//            rate = 5.0f,
//            shopImage = "Image"
//        ).apply { id = 1L }
//    }
//}