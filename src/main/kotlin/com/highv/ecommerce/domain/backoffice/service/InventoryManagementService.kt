package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeResponse
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InventoryManagementService(
    private val productBackOfficeRepository: ProductBackOfficeRepository,
) {
    fun getProductQuantity(productId: Long): ProductBackOfficeResponse {
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product not found")
        return ProductBackOfficeResponse(product.id, product.quantity, product.price)
    }

    fun changeQuantity(productId: Long, quantity: Int): ProductBackOfficeResponse {
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        product.quantity = quantity
        val changedProduct = productBackOfficeRepository.save(product)
        return ProductBackOfficeResponse.from(changedProduct)
    }

    fun changePrice(productId: Long, price: Int): ProductBackOfficeResponse {
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        product.price = price
        val changedProduct = productBackOfficeRepository.save(product)
        return ProductBackOfficeResponse.from(changedProduct)
    }
}