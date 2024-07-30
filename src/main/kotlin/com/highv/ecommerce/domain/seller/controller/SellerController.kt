package com.highv.ecommerce.domain.seller.controller

import com.highv.ecommerce.common.exception.LoginException
import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.service.SellerService
import com.highv.ecommerce.domain.seller.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/seller")
class SellerController(private val sellerService: SellerService) {
    @PostMapping("/user_signup")
    fun signUp(
        @RequestPart @Valid request: CreateSellerRequest,
        bindingResult: BindingResult,
        @RequestPart(value = "file", required = false) file: MultipartFile?,
    ): ResponseEntity<SellerResponse> {

        if (bindingResult.hasErrors()) {
            throw LoginException(bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(sellerService.signUp(request, file))
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    fun createShop(
        @AuthenticationPrincipal seller: UserPrincipal,
        @RequestPart createShopRequest: CreateShopRequest,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(sellerService.createShop(seller.id, createShopRequest, file))
}