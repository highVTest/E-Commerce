package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
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
    fun getTotalSales(sellerId: Long): TotalSalesResponse {
        val products = productRepository.findAllByShopId(sellerId)
        val productIds = products.mapNotNull { it.id }
        val totalSales = productBackOfficeRepository.findTotalSalesStatisticsByProductIds(productIds)
        return TotalSalesResponse(totalSales.totalQuantity, totalSales.totalPrice)
    }

    fun getMonthsProductSales(sellerId: Long): List<TotalSalesResponse> {
        TODO("최근 1년의 판매를 기준으로 월별 표기. 1월부터 ~ 12월까지 표기")
    }

    fun getProductSales(sellerId: Long, productId: Long): ProductSalesResponse {
        val product = validateProduct(sellerId, productId)
        val productBackOffice = product.productBackOffice
            ?: throw IllegalArgumentException("ProductBackOffice not found for product with ID $productId")
        return ProductSalesResponse(
            product.name,
            productBackOffice.soldQuantity,
            (productBackOffice.soldQuantity * productBackOffice.price)
        )
    }

    private fun validateProduct(sellerId: Long, productId: Long): Product {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Product with ID $productId not found")
        if (product.shop.sellerId != sellerId) throw IllegalArgumentException("No Authority")
        return product
    }
}