package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.backoffice.service.SalesStatisticsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/seller")
class SalesStatisticsController(
    private val salesStatisticsService: SalesStatisticsService
) {
    //총 판매량 조회
    @GetMapping("/total-sales-quantity")
    fun getTotalSales(): ResponseEntity<TotalSalesQuantityResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getTotalSalesQuantity())

    //총 매출액 조회
    @GetMapping("/total-sales-amount")
    fun getTotalSalesAmount(): ResponseEntity<TotalSalesResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getTotalSalesAmount())

    //상품별 판매량 조회
    @GetMapping("/{productId}")
    fun getProductSales(
        @PathVariable productId: Long
    ): ResponseEntity<ProductSalesQuantityResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getProductSalesQuantity(productId))

    //상품별 매출액 조회
    @GetMapping("/{productId}/sales-amount")
    fun getProductSalesAmount(
        @PathVariable productId: Long
    ): ResponseEntity<ProductSalesResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getProductSales(productId))

    //각 상품별 재고 조회
    @GetMapping("/{productId}/products")
    fun getProducts(
        @PathVariable productId: Long
    ): ResponseEntity<ProductQuantityResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getProductsQuantity(productId))
}