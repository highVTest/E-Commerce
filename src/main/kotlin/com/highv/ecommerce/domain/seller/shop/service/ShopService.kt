package com.highv.ecommerce.domain.seller.shop.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.seller.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import org.springframework.stereotype.Service

@Service
class ShopService(private val shopRepository: ShopRepository) {

    fun createShop(sellerId: Long, createShopRequest: CreateShopRequest): ShopResponse {
        if (shopRepository.existsBySellerId(sellerId)) throw CustomRuntimeException(409, "Shop with seller id $sellerId already exists")
        val shop = Shop(
            sellerId = sellerId,
            name = createShopRequest.name,
            description = createShopRequest.description,
            shopImage = createShopRequest.shopImage,
            rate = 0.0f
        )
        val savedShop = shopRepository.save(shop)
        return ShopResponse.from(savedShop)
    }

    fun getShopById(sellerId: Long): ShopResponse {
        val shop = shopRepository.findShopBySellerId(sellerId)
        return ShopResponse.from(shop)
    }
}