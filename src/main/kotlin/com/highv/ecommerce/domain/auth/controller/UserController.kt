package com.highv.ecommerce.domain.auth.controller

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/login")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun signIn(@RequestBody loginRequest: LoginRequest): ResponseEntity<AccessTokenResponse> =
        ResponseEntity.ok().body(userService.login(loginRequest))
}