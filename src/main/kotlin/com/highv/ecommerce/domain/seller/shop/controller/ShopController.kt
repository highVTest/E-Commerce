package com.highv.ecommerce.domain.seller.shop.controller

import com.highv.ecommerce.domain.seller.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.domain.seller.shop.service.ShopService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/seller/shop")
class ShopController(private val shopService: ShopService) {

    //생성
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    fun createShop(
        @AuthenticationPrincipal seller: UserPrincipal,
        createShopRequest: CreateShopRequest,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(shopService.createShop(seller.id, createShopRequest,file))

    //조회
    @GetMapping
    fun getShopById(
        @AuthenticationPrincipal seller: UserPrincipal
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(shopService.getShopById(seller.id))
}