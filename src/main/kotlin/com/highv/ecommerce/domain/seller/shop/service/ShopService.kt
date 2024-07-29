package com.highv.ecommerce.domain.seller.shop.service

import com.highv.ecommerce.domain.seller.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import com.highv.ecommerce.infra.s3.S3Manager
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ShopService(
    private val shopRepository: ShopRepository,
    private val s3Manager: S3Manager
) {

    fun createShop(sellerId: Long, createShopRequest: CreateShopRequest, file: MultipartFile?): ShopResponse {
        if (shopRepository.existsBySellerId(sellerId)) throw RuntimeException("Shop with seller id $sellerId already exists")
        val shop = Shop(
            sellerId = sellerId,
            name = createShopRequest.name,
            description = createShopRequest.description,
            shopImage = "",
            rate = 0.0f
        )
        if (file != null) {
            s3Manager.uploadFile(file) // S3Manager를 통해 파일 업로드
            shop.shopImage = s3Manager.getFile(file.originalFilename)
        }
        val savedShop = shopRepository.save(shop)
        return ShopResponse.from(savedShop)
    }

    fun getShopById(sellerId: Long): ShopResponse {
        val shop = shopRepository.findShopBySellerId(sellerId)
        return ShopResponse.from(shop)
    }
}