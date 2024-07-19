package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesQuantityResponse
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
    fun getTotalSalesQuantity(sellerId: Long): TotalSalesQuantityResponse {
        val products = productBackOfficeRepository.findAllBySellerId(sellerId)
        val totalSalesQuantity = products.sumOf { it.quantity }
        return TotalSalesQuantityResponse(totalSalesQuantity)
    }

    fun getTotalSalesAmount(sellerId: Long): TotalSalesResponse {
        val products = productBackOfficeRepository.findAllBySellerId(sellerId)
        val totalSalesAmount = products.sumOf { it.soldQuantity * it.price }
        return TotalSalesResponse(totalSalesAmount)
    }

    fun getProductSalesQuantity(sellerId: Long, productId: Long): ProductSalesQuantityResponse {
        val productName = productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product with ID $productId not found")
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product with ID $productId not found")
        if (productName.shopId != sellerId) throw IllegalArgumentException("No Authority")
        return ProductSalesQuantityResponse(productName.name, product.soldQuantity)
    }

    fun getProductSales(sellerId: Long, productId: Long): ProductSalesResponse {
        val productName = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        val product = productBackOfficeRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product with ID $productId not found")
        if (productName.shopId != sellerId) throw IllegalArgumentException("No Authority")
        return ProductSalesResponse(productName.name, product.soldQuantity * product.price)
    }
}