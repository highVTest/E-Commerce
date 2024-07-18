package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateSellerRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateShopRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.shop.dto.ShopResponse
import com.highv.ecommerce.domain.shop.repository.ShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SellerInfoService(
    private val shopRepository: ShopRepository,
    private val sellerRepository: SellerRepository
) {
    fun updateShopInfo(sellerId: Long, updateShopRequest: UpdateShopRequest): ShopResponse {
        val shop = shopRepository.findByIdOrNull(sellerId) ?: throw RuntimeException("Shop not found")
        shop.apply {
            description = updateShopRequest.description
            shopImage = updateShopRequest.shopImage
        }
        val updatedShop = shopRepository.save(shop)
        return ShopResponse.from(updatedShop)
    }

    fun updateSellerInfo(sellerId: Long, updateSellerRequest: UpdateSellerRequest): SellerResponse {
        TODO()
    }
}