package com.highv.ecommerce.domain.buyer.dto.response

import com.highv.ecommerce.domain.buyer.entity.Buyer

data class BuyerResponse(
    val id: Long?,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val phoneNumber: String,
    val address: String
) {
    companion object {
        fun from(buyer: Buyer): BuyerResponse {
            return BuyerResponse(
                id = buyer.id,
                email = buyer.email,
                nickname = buyer.nickname,
                profileImage = buyer.profileImage,
                phoneNumber = buyer.phoneNumber,
                address = buyer.address
            )
        }
    }
}