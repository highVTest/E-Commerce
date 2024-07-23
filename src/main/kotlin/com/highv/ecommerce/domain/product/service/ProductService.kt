package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.shop.repository.ShopRepository
import com.highv.ecommerce.s3.config.S3Manager
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
    private val s3Manager: S3Manager
) {
    fun createProduct(sellerId: Long, productRequest: CreateProductRequest,multipartFile: MultipartFile?): ProductResponse {
        val shop = shopRepository.findShopBySellerId(sellerId)
        val product = Product(
            name = productRequest.name,
            description = productRequest.description,
            productImage = productRequest.productImage,
            favorite = 0,
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
            quantity = 0,
            price = 0,
            soldQuantity = 0,
            product = savedProduct
        )
        savedProduct.productBackOffice = productBackOffice
        productBackOfficeRepository.save(productBackOffice)

        if (multipartFile != null) {        // 파일 업로드 처리
            val fileUrl = s3Manager.uploadFile(multipartFile) // S3Manager를 통해 파일 업로드
            savedProduct.productImage = fileUrl // 저장된 상품에 파일 URL 저장
            productRepository.save(savedProduct)
        }// 변경 사항 저장

        return ProductResponse.from(savedProduct)
    }

    fun updateProduct(sellerId: Long, productId: Long, updateProductRequest: UpdateProductRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        if (product.shop.sellerId != sellerId) throw RuntimeException("No Authority")
        product.apply {
            name = updateProductRequest.name
            description = updateProductRequest.description
            productImage = updateProductRequest.productImage
            updatedAt = LocalDateTime.now()
            isSoldOut = updateProductRequest.isSoldOut
            categoryId = updateProductRequest.categoryId
        }
        val updatedProduct = productRepository.save(product)
        return ProductResponse.from(updatedProduct)
    }

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
        return ProductResponse.from(product)
    }

    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findAllPaginated(pageable)
        return products.map { ProductResponse.from(it) }
    }

    fun getProductsByCategory(categoryId: Long, pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findByCategoryPaginated(categoryId, pageable)
        return products.map { ProductResponse.from(it) }
    }

    fun searchProduct(keyword: String, pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.searchByKeywordPaginated(keyword, pageable)
        if (products.hasContent()) {
            return products.map { ProductResponse.from(it) }
        }
        return Page.empty(pageable)
    }
}