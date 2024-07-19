package com.highv.ecommerce.domain.buyer.controller

import com.highv.ecommerce.common.exception.LoginException
import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.response.BuyerResponse
import com.highv.ecommerce.domain.buyer.service.BuyerService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/buyer")
class BuyerController(private val buyerService: BuyerService) {
    @PostMapping("/user_signup")
    fun signUp(
        @RequestBody @Valid request: CreateBuyerRequest,
        bindingResult: BindingResult
    ): ResponseEntity<BuyerResponse> {

        if (bindingResult.hasErrors()) {
            throw LoginException(bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(buyerService.signUp(request))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/profile/password")
    fun changePassword(
        @Valid @RequestBody request: UpdateBuyerPasswordRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal user: UserPrincipal,
    ): ResponseEntity<Unit> {

        if (bindingResult.hasErrors()) {
            throw RuntimeException(bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(buyerService.changePassword(request, user.id))
    }
}