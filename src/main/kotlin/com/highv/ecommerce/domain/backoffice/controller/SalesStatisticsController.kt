package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.domain.backoffice.dto.salesstatics.ProductSalesResponse
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
    //전체 상품 판매량 + 금액
    @GetMapping("/total-sales")
    @PreAuthorize("hasRole('SELLER')")
    fun getTotalSales(
        @AuthenticationPrincipal seller: UserPrincipal,
    ): ResponseEntity<TotalSalesResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getTotalSales(seller.id))


    //상품 판매 수량 및 금액
    @GetMapping("/sales")
    @PreAuthorize("hasRole('SELLER')")
    fun getProductSales(
        @AuthenticationPrincipal seller: UserPrincipal,
    ): ResponseEntity<List<ProductSalesResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(salesStatisticsService.getProductSales(seller.id))
}