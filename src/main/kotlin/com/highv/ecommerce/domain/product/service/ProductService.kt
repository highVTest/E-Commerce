package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeRequest
import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import com.highv.ecommerce.infra.s3.S3Manager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository,
    private val productBackOfficeRepository: ProductBackOfficeRepository,
    private val favoriteService: FavoriteService,
    private val s3Manager: S3Manager,
) {

    fun createProduct(
        sellerId: Long,
        productRequest: CreateProductRequest,
        productBackOfficeRequest: ProductBackOfficeRequest,
        multipartFile: MultipartFile
    ): ProductResponse {

        s3Manager.uploadFile(multipartFile) // S3Manager를 통해 파일 업로드

        val shop = shopRepository.findShopBySellerId(sellerId)
        val product = Product(
            name = productRequest.name,
            description = productRequest.description,
            productImage = s3Manager.getFile(multipartFile.originalFilename), // Buyer 객체에 프로필 이미지 URL 저장
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
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

        return ProductResponse.from(savedProduct)
    }

    fun updateProduct(sellerId: Long, productId: Long, updateProductRequest: UpdateProductRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw CustomRuntimeException(404, "Product not found")
        if (product.shop.sellerId != sellerId) throw CustomRuntimeException(403, "No Authority")
        product.apply {
            name = updateProductRequest.name
            description = updateProductRequest.description
            productImage = updateProductRequest.productImage
            updatedAt = LocalDateTime.now()
            isSoldOut = updateProductRequest.isSoldOut
            categoryId = updateProductRequest.categoryId
        }
        val updatedProduct = productRepository.save(product)
        return ProductResponse.from(updatedProduct, favoriteService.countFavorite(productId))
    }

    fun deleteProduct(sellerId: Long, productId: Long) {
        val product = productRepository.findByIdOrNull(productId) ?: throw CustomRuntimeException(404, "Product not found")
        if (product.shop.sellerId != sellerId) throw CustomRuntimeException(403, "No Authority")
        product.apply {
            isDeleted = true
            deletedAt = LocalDateTime.now()
        }
        productRepository.save(product)
    }

    fun getProductById(productId: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw CustomRuntimeException(404, "Product not found")
        return ProductResponse.from(product, favoriteService.countFavorite(productId))
    }

    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findAllPaginated(pageable)
        return products.map { ProductResponse.from(it, favoriteService.countFavorite(it.id!!)) }
    }

    fun getProductsByCategory(categoryId: Long, pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findByCategoryPaginated(categoryId, pageable)
        return products.map { ProductResponse.from(it, favoriteService.countFavorite(it.id!!)) }
    }
}