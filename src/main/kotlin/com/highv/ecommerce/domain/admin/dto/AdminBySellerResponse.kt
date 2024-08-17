package com.highv.ecommerce.domain.admin.dto

import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.domain.seller.shop.entity.Shop

data class AdminBySellerResponse(
    val id: Long?,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val phoneNumber: String,
    val address: String,
    val activeStatus: ActiveStatus,
    val shop: ShopResponse
){
    companion object{
        fun from(shop: Shop, seller: Seller) = AdminBySellerResponse(
            id = seller.id!!,
            email = seller.email,
            nickname = seller.nickname,
            profileImage = seller.profileImage,
            phoneNumber = seller.phoneNumber,
            address = seller.address,
            activeStatus = seller.activeStatus,
            shop = ShopResponse.from(shop)
        )
    }
}