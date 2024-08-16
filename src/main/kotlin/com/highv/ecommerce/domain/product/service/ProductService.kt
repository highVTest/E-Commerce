package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.common.lock.service.RedisLockService
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeRequest
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.ProductSummaryResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository,
    private val sellerRepository: SellerRepository,
    private val productBackOfficeRepository: ProductBackOfficeRepository,
    private val favoriteService: FavoriteService,
    private val redisLockService: RedisLockService,
) {
    @Transactional
    fun createProduct(
        sellerId: Long,
        productRequest: CreateProductRequest,
        productBackOfficeRequest: ProductBackOfficeRequest,
    ): ProductResponse {
        val lockKey = "createProduct:${sellerId}:${productRequest.name}"
        return redisLockService.runExclusiveWithRedissonLock(lockKey, 5) {

            val seller = sellerRepository.findByIdOrNull(sellerId)
                ?: throw RuntimeException("Seller not found")

            if (seller.activeStatus == ActiveStatus.PENDING || seller.activeStatus == ActiveStatus.RESIGNED) {
                throw RuntimeException("Seller is not authorized to create a product")
            }
            val shop = shopRepository.findShopBySellerId(sellerId)

            if (productRepository.existsByNameAndShopId(
                    productRequest.name,
                    shop.id!!
                )
            ) throw RuntimeException("중복 상품 입니다.")

            val product = Product(
                name = productRequest.name,
                description = productRequest.description,
                productImage = productRequest.imageUrl,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isSoldOut = false,
                deletedAt = null,
                isDeleted = false,
                shop = shop,
                categoryId = productRequest.categoryId,
                productBackOffice = null
            )

            val savedProduct = productRepository.save(product)
            val productBackOffice = ProductBackOffice(
                quantity = productBackOfficeRequest.quantity,
                price = productBackOfficeRequest.price,
                soldQuantity = 0,
                product = savedProduct
            )
            savedProduct.productBackOffice = productBackOffice
            productBackOfficeRepository.save(productBackOffice)
            productRepository.save(savedProduct)
            ProductResponse.from(savedProduct)
        }
    }

    @Transactional
    fun updateProduct(
        sellerId: Long,
        productId: Long,
        updateProductRequest: UpdateProductRequest,
    ): ProductResponse {

        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")

        if (product.shop.sellerId != sellerId) throw RuntimeException("No Authority")

        if (productRepository.existsByNameAndShopId(
                updateProductRequest.name,
                product.shop.id!!
            )
        ) throw RuntimeException("중복 상품명 입니다.")

        product.apply {
            name = updateProductRequest.name
            description = updateProductRequest.description
            productImage = ""
            updatedAt = LocalDateTime.now()
            isSoldOut = updateProductRequest.isSoldOut
            categoryId = updateProductRequest.categoryId
        }

        val updatedProduct = productRepository.save(product)
        return ProductResponse.from(updatedProduct, favoriteService.countFavorite(productId))
    }

    @Transactional
    fun deleteProduct(sellerId: Long, productId: Long) {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        if (product.shop.sellerId != sellerId) throw RuntimeException("No Authority")
        product.apply {
            isDeleted = true
            deletedAt = LocalDateTime.now()
        }
        productRepository.save(product)
    }

    fun getProductById(productId: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        return ProductResponse.from(product, favoriteService.countFavorite(productId))
    }

    fun getProductsByCategory(categoryId: Long, pageable: Pageable): Page<ProductSummaryResponse> {
        val products = productRepository.findByCategoryPaginated(categoryId, pageable)
        return products.map { ProductSummaryResponse.from(it, favoriteService.countFavorite(it.id)) }
    }
}
