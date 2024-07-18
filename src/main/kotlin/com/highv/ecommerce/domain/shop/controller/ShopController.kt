package com.highv.ecommerce.domain.shop.controller

import com.highv.ecommerce.domain.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.shop.dto.ShopResponse
import com.highv.ecommerce.domain.shop.service.ShopService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/{sellerId}/shop")
class ShopController(private val shopService: ShopService) {

    //생성
    @PostMapping
    fun createShop(
        @PathVariable("sellerId") sellerId: Long,
        createShopRequest: CreateShopRequest
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(shopService.createShop(sellerId, createShopRequest))

    //조회
    @GetMapping
    fun getShopById(
        @PathVariable("sellerId") sellerId: Long,
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(shopService.getShopById(sellerId))
}