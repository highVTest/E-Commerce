package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class SalesStatisticsService(
    private val productBackOfficeRepository: ProductBackOfficeRepository,
    private val productRepository: ProductRepository
) {

    fun getTotalSalesQuantity(sellerId: Long): TotalSalesQuantityResponse {
        val products = productRepository.findAllByShopId(sellerId)
        val productIds = products.mapNotNull { it.id }
        val totalSalesQuantity =
            if (productIds.isEmpty()) 0 else productBackOfficeRepository.findTotalSoldQuantitiesByProductIds(productIds)

        return TotalSalesQuantityResponse(totalSalesQuantity)
    }

    fun getTotalSalesAmount(sellerId: Long): TotalSalesResponse {
        val products = productRepository.findAllByShopId(sellerId)
        val productIds = products.mapNotNull { it.id }
        val totalSalesAmount =
            if (productIds.isEmpty()) 0 else productBackOfficeRepository.findTotalSalesAmountByProductIds(productIds)
        return TotalSalesResponse(totalSalesAmount)
    }

    fun getProductSalesQuantity(sellerId: Long, productId: Long): ProductSalesQuantityResponse {
        val product = validateProductWithBackOffice(sellerId, productId)
        val productBackOffice = product.productBackOffice
            ?: throw IllegalArgumentException("ProductBackOffice not found for product with ID $productId")
        return ProductSalesQuantityResponse(product.name, productBackOffice.soldQuantity.toInt())
    }

    fun getProductSales(sellerId: Long, productId: Long): ProductSalesResponse {
        val product = validateProductWithBackOffice(sellerId, productId)
        val productBackOffice = product.productBackOffice
            ?: throw IllegalArgumentException("ProductBackOffice not found for product with ID $productId")
        return ProductSalesResponse(product.name, (productBackOffice.soldQuantity * productBackOffice.price).toInt())
    }

    private fun validateProductWithBackOffice(sellerId: Long, productId: Long): Product {
        // val product = productRepository.findProductWithBackOfficeById(productId)
        //     ?: throw IllegalArgumentException("Product with ID $productId not found")
        // if (product.shop.sellerId != sellerId) {
        //     throw IllegalArgumentException("No Authority")
        // }

        val product = productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product with ID $productId not found")

        if (product.shop.sellerId != sellerId) {
            throw IllegalArgumentException("No Authority")
        }
        
        return product
    }
}