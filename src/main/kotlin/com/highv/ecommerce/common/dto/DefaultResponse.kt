package com.highv.ecommerce.common.dto

data class DefaultResponse(
    val msg: String,
){
    companion object {
        fun from(message: String) = DefaultResponse(msg = message)
    }
}