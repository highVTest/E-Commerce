package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesQuantityResponse
import com.highv.ecommerce.domain.backoffice.dto.salesstatics.TotalSalesResponse
import com.highv.ecommerce.domain.backoffice.service.SalesStatisticsService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    @PreAuthorize("hasRole('SELLER')")
    fun getTotalSales(
        @AuthenticationPrincipal seller: UserPrincipal,
    ): ResponseEntity<TotalSalesQuantityResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getTotalSalesQuantity(seller.id))

    //총 매출액 조회
    @GetMapping("/total-sales-amount")
    @PreAuthorize("hasRole('SELLER')")
    fun getTotalSalesAmount(
        @AuthenticationPrincipal seller: UserPrincipal,
    ): ResponseEntity<TotalSalesResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getTotalSalesAmount(seller.id))

    //상품별 판매량 조회
    @GetMapping("/{productId}/sales-quantity")
    @PreAuthorize("hasRole('SELLER')")
    fun getProductSales(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<ProductSalesQuantityResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getProductSalesQuantity(seller.id, productId))

    //상품별 매출액 조회
    @GetMapping("/{productId}/sales-amount")
    @PreAuthorize("hasRole('SELLER')")
    fun getProductSalesAmount(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<ProductSalesResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getProductSales(seller.id, productId))
}