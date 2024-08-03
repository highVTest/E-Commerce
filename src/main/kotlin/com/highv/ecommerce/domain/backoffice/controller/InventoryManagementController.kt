package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.PriceRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.ProductBackOfficeResponse
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.QuantityRequest
import com.highv.ecommerce.domain.backoffice.dto.productbackoffice.SellersProductResponse
import com.highv.ecommerce.domain.backoffice.service.InventoryManagementService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/seller")
class InventoryManagementController(
    private val inventoryManagementService: InventoryManagementService
) {
    @GetMapping("/{productId}/left-quantity")
    @PreAuthorize("hasRole('SELLER')")
    fun getProductsQuantity(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<ProductBackOfficeResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.getProductsQuantity(seller.id, productId))

    @PatchMapping("/{productId}/quantity")
    @PreAuthorize("hasRole('SELLER')")
    fun changeQuantity(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long,
        @RequestBody quantity: QuantityRequest
    ): ResponseEntity<ProductBackOfficeResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.changeQuantity(seller.id, productId, quantity))

    @PatchMapping("/{productId}/price")
    @PreAuthorize("hasRole('SELLER')")
    fun changePrice(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long,
        @RequestBody price: PriceRequest
    ): ResponseEntity<ProductBackOfficeResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.changePrice(seller.id, productId, price))

    @GetMapping("/products")
    @PreAuthorize("hasRole('SELLER')")
    fun getSellerProducts(
        @AuthenticationPrincipal seller: UserPrincipal
    ): ResponseEntity<List<SellersProductResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.getSellerProducts(seller.id))
}