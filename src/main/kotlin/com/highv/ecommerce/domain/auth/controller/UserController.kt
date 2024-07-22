package com.highv.ecommerce.domain.auth.controller

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.dto.UserRole
import com.highv.ecommerce.domain.auth.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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

    @PostMapping("/emails/verification-request")
    fun sendMail(
        @RequestParam(value = "email") email: String,
        @RequestParam(value = "role") role: UserRole
    ): ResponseEntity<String> = ResponseEntity
        .status(HttpStatus.OK)
        .body(userService.sendMail(email, role))

    @GetMapping("/emails/verifications")
    fun verifyEmail(
        @RequestParam(value = "email") email: String,
        @RequestParam(value = "code") code: String,
        @RequestParam(value = "role") role: UserRole
    ): ResponseEntity<Boolean> = ResponseEntity
        .status(HttpStatus.OK)
        .body(userService.verifyCode(email, code, role))
}