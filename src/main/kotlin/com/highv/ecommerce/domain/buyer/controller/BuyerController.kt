package com.highv.ecommerce.domain.buyer.controller

import com.highv.ecommerce.common.exception.LoginException
import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
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
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/buyer")
class BuyerController(private val buyerService: BuyerService) {

    @PostMapping("/user_signup")
    fun signUp(
        @RequestPart @Valid request: CreateBuyerRequest,
        bindingResult: BindingResult,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ResponseEntity<BuyerResponse> {

        if (bindingResult.hasErrors()) {
            throw LoginException(bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(buyerService.signUp(request, file))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/profile/pw")
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

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/profile/image")
    fun changeImage(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.OK)
        .body(buyerService.changeProfileImage(user.id, file))

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/profile")
    fun changeProfile(
        @RequestBody request: UpdateBuyerProfileRequest,
        @AuthenticationPrincipal user: UserPrincipal,
    ): ResponseEntity<BuyerResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(buyerService.changeProfile(request, user.id))
}