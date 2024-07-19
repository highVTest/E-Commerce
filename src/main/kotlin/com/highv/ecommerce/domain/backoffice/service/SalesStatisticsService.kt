package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SalesStatisticsService(
    private val productBackOfficeRepository: ProductBackOfficeRepository,
    private val productRepository: ProductRepository
) {

    fun getTotalSales(): TotalSalesResponse {
        val products = productBackOfficeRepository.findAll()
        val totalSalesAmount = products.sumOf { it.soldQuantity * it.price }
        return TotalSalesResponse(totalSalesAmount)
    }

    fun getProductSales(productId: Long): ProductSalesResponse {
        val productName = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        return ProductSalesResponse(productName.name, product.soldQuantity * product.price)
    }

    fun getProductsQuantity(productId: Long): ProductQuantityResponse {
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        return ProductQuantityResponse(product.quantity, product.soldQuantity)
    }
}