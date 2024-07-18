package com.highv.ecommerce.domain.buyer.controller

import com.highv.ecommerce.domain.buyer.dto.BuyerResponse
import com.highv.ecommerce.domain.buyer.dto.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.service.BuyerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/buyer")
class BuyerController(private val buyerService: BuyerService) {
    @PostMapping("/user_signup")
    fun signUp(@RequestBody @Valid request: CreateBuyerRequest): ResponseEntity<BuyerResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(buyerService.signUp(request))
}