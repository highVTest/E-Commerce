package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.domain.backoffice.dto.ProductBackOfficeResponse
import com.highv.ecommerce.domain.backoffice.service.InventoryManagementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class InventoryManagementController(
    private val inventoryManagementService: InventoryManagementService
) {
    //재고 수량 확인
    @GetMapping("/{productId}")
    fun getProductQuantity(
        @PathVariable("productId") productId: Long
    ): ResponseEntity<ProductBackOfficeResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.getProductQuantity(productId))

    //재고 수량 변경
    @PatchMapping("/{productId}")
    fun changeQuantity(
        @PathVariable productId: Long,
        quantity: Int
    ): ResponseEntity<ProductBackOfficeResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.changeQuantity(productId, quantity))

    //상품 가격 변경
    @PatchMapping("/{productId}")
    fun changePrice(
        @PathVariable productId: Long,
        price: Int
    ): ResponseEntity<ProductBackOfficeResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(inventoryManagementService.changePrice(productId, price))
}