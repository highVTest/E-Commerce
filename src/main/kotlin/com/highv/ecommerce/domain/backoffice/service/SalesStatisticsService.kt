package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.stereotype.Service

@Service
class SalesStatisticsService(
    private val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository
) {
    fun getTotalSales(sellerId: Long): TotalSalesResponse {
        val shop = shopRepository.findBySellerId(sellerId)
        val totalSales = orderDetailsRepository.totalSalesStatisticsByShop(shop.id!!)
        return TotalSalesResponse(totalSales.totalQuantity, totalSales.totalPrice)
    }

    fun getMonthsProductSales(sellerId: Long): List<TotalSalesResponse> {
        TODO("최근 1년의 판매를 기준으로 월별 표기. 1월부터 ~ 12월까지 표기")
    }

    fun getProductSales(sellerId: Long): List<ProductSalesResponse> {
        val products = validateProduct(sellerId)
        products.forEach {
            if (it.productBackOffice == null) throw CustomRuntimeException(404, "backoffice is null")
        }

        return products.map {
            ProductSalesResponse(
                it.name,
                it.productBackOffice!!.soldQuantity,
                (it.productBackOffice!!.soldQuantity * it.productBackOffice!!.price),
            )
        }
    }

    private fun validateProduct(sellerId: Long): List<Product> {
        val shop = shopRepository.findBySellerId(sellerId)
        val products = productRepository.findAllByShopId(shop.id!!)
        return products
    }
}