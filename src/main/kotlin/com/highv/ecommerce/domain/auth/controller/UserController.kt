package com.highv.ecommerce.domain.auth.controller

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.ValidationException
import com.highv.ecommerce.domain.auth.dto.EmailAuthRequest
import com.highv.ecommerce.domain.auth.dto.EmailAuthResponse
import com.highv.ecommerce.domain.auth.dto.ImageUrlResponse
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.service.UserService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/login/seller")
    fun loginSeller(@RequestBody loginRequest: LoginRequest): ResponseEntity<AccessTokenResponse> =
        ResponseEntity.ok().body(userService.loginSeller(loginRequest))

    @PostMapping("/login/buyer")
    fun loginBuyer(@RequestBody loginRequest: LoginRequest): ResponseEntity<AccessTokenResponse> =
        ResponseEntity.ok().body(userService.loginBuyer(loginRequest))

    // email 컨트롤러 분리하기? 구매자, 판매자
    @PostMapping("/email/send")
    fun sendMail(
        @RequestBody request: EmailAuthRequest,
        bindingResult: BindingResult
    ): ResponseEntity<DefaultResponse> {

        if (bindingResult.hasErrors()) {
            throw ValidationException(message = bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.sendMail(request.email, request.role))
    }

    @PostMapping("/email/confirm")
    fun verifyEmail(
        @RequestBody request: EmailAuthRequest,
        bindingResult: BindingResult,
        @RequestParam(value = "code") code: String
    ): ResponseEntity<EmailAuthResponse> {

        if (bindingResult.hasErrors()) {
            throw ValidationException(message = bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.verifyCode(request.email, request.role, code))
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @PostMapping("/image")
    fun uploadImage(
        @RequestPart file: MultipartFile,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<ImageUrlResponse> = ResponseEntity.ok(userService.uploadImage(file, user.id))

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @PostMapping("/images")
    fun uploadImages(
        @RequestPart files: List<MultipartFile>,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<List<ImageUrlResponse>> = ResponseEntity.ok(userService.uploadImages(files, user.id))
}