package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdatePasswordRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateSellerRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateShopRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SellerInfoService(
    private val shopRepository: ShopRepository,
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun updateShopInfo(sellerId: Long, updateShopRequest: UpdateShopRequest): ShopResponse {
        val shop = shopRepository.findShopBySellerId(sellerId)
        shop.apply {
            description = updateShopRequest.description
            shopImage = updateShopRequest.shopImage
        }
        val updatedShop = shopRepository.save(shop)
        return ShopResponse.from(updatedShop)
    }

    fun updateSellerInfo(sellerId: Long, updateSellerRequest: UpdateSellerRequest): SellerResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId) ?: throw CustomRuntimeException(404, "Seller not found")
        seller.apply {
            address = updateSellerRequest.address
            nickname = updateSellerRequest.nickname
            phoneNumber = updateSellerRequest.phoneNumber
            profileImage = updateSellerRequest.profileImage
        }
        val updateSellerInfo = sellerRepository.save(seller)
        return SellerResponse.from(updateSellerInfo)
    }

    fun changePassword(sellerId: Long, updatePasswordRequest: UpdatePasswordRequest): String {
        val seller = sellerRepository.findByIdOrNull(sellerId) ?: throw CustomRuntimeException(404, "Seller not found")
        if (passwordEncoder.matches(
                passwordEncoder.encode(updatePasswordRequest.oldPassword),
                seller.password
            )
        ) throw RuntimeException("Old password not matched")
        seller.apply {
            password = passwordEncoder.encode(updatePasswordRequest.newPassword)
        }
        sellerRepository.save(seller)
        return "Password 변경 완료"
    }
}