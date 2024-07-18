package com.highv.ecommerce.domain.seller.controller

import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.service.SellerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/seller")
class SellerController(private val sellerService: SellerService) {
    @PostMapping("/user_signup")
    fun signUp(@RequestBody @Valid request: CreateSellerRequest): ResponseEntity<SellerResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(sellerService.signUp(request))
}