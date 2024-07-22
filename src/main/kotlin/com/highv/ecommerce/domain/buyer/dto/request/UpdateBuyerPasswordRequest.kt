package com.highv.ecommerce.domain.buyer.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateBuyerPasswordRequest(

    val currentPassword: String,
    @field : NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @field : Size(min = 8, max = 30, message = "비밀번호는 최소 8자 이상 최대 30 이하입니다.")
    val newPassword: String,
    @field : NotBlank(message = "확인 비밀번호는 공백일 수 없습니다.")
    @field : Size(min = 8, max = 30, message = "확인 비밀번호는 최소 8자 이상 최대 30 이하입니다.")
    val confirmNewPassword: String
)
