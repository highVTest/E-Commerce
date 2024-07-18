package com.highv.ecommerce.domain.seller.dto

import com.highv.ecommerce.domain.seller.entity.Seller

data class SellerResponse(
    val id: Long?,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val phoneNumber: String,
    val address: String
) {
    companion object {
        fun from(seller: Seller): SellerResponse {
            return SellerResponse(
                id = seller.id,
                email = seller.email,
                nickname = seller.nickname,
                profileImage = seller.profileImage,
                phoneNumber = seller.phoneNumber,
                address = seller.address
            )
        }
    }

}
