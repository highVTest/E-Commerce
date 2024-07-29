package com.highv.ecommerce.common.exception.handler

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.exception.LoginException
import com.highv.ecommerce.common.exception.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 모든 RuntimeException을 처리하는 핸들러
    @ExceptionHandler(RuntimeException::class)
    fun allException(e: RuntimeException): ResponseEntity<ErrorResponse> {
        // HttpStatus.BAD_REQUEST 상태 코드와 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(409, e.message ?: "Unknown error occurred"))
    }

    // LoginException을 처리하는 핸들러
    @ExceptionHandler(LoginException::class)
    fun loginExceptionHandler(e: LoginException): ResponseEntity<ErrorResponse> {
        // HttpStatus.BAD_REQUEST 상태 코드와 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(400, e.message))
    }

    // CustomRuntimeException을 처리하는 핸들러
    @ExceptionHandler(CustomRuntimeException::class)
    fun customRuntimeExceptionHandler(e: CustomRuntimeException): ResponseEntity<ErrorResponse> {
        // HttpStatus.BAD_REQUEST 상태 코드와 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message ?: "Unknown error occurred"))
    }
}