package com.highv.ecommerce.common.exception.dto

data class ErrorResponse(
    val errorCode: Int,
    val errorMessage: String,
)