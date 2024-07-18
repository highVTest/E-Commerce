package com.highv.ecommerce.common.exception.handler

import com.highv.ecommerce.common.exception.LoginException
import com.highv.ecommerce.common.exception.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun allException(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(409, e.message ?: "???"))
    }

    @ExceptionHandler(LoginException::class)
    fun loginExceptionHandler(e: LoginException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(400, e.message))
    }
}