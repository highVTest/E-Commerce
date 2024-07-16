package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun createProduct(productRequest: CreateProductRequest): ProductResponse {
        val product = Product(
            name = productRequest.name,
            description = productRequest.description,
            price = productRequest.price,
            productImage = productRequest.productImage,
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            quantity = productRequest.quantity,
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shopId = productRequest.shopId,
            categoryId = productRequest.categoryId
        )
        val savedProduct = productRepository.save(product)
        return ProductResponse.from(savedProduct)
    }

    fun updateProduct(productId: Long,updateProductRequest: UpdateProductRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        product.apply {
            name = updateProductRequest.name
            description = updateProductRequest.description
            price = updateProductRequest.price
            productImage = updateProductRequest.productImage
            updatedAt = LocalDateTime.now()
            quantity = updateProductRequest.quantity
            isSoldOut = updateProductRequest.isSoldOut
            categoryId = updateProductRequest.categoryId
        }
        val updatedProduct = productRepository.save(product)
        return ProductResponse.from(updatedProduct)

    }

    fun deleteProduct(productId: Long) {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
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
}