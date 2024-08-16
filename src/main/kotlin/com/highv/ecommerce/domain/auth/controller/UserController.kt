package com.highv.ecommerce.domain.auth.controller

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.ValidationException
import com.highv.ecommerce.domain.auth.dto.EmailAuthRequest
import com.highv.ecommerce.domain.auth.dto.EmailAuthResponse
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
        @Valid @RequestBody request: EmailAuthRequest,
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
        @Valid @RequestBody request: EmailAuthRequest,
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
}