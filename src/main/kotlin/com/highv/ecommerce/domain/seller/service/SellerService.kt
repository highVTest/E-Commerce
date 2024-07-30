package com.highv.ecommerce.domain.seller.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.exception.EmailVerificationNotFoundException
import com.highv.ecommerce.common.exception.UnverifiedEmailException
import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository

import com.highv.ecommerce.infra.s3.S3Manager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val s3Manager: S3Manager,
    private val shopRepository: ShopRepository
) {

    fun signUp(request: CreateSellerRequest, file: MultipartFile?): SellerResponse {

        val seller: Seller =
            sellerRepository.findByIdOrNull(request.id) ?: throw EmailVerificationNotFoundException(404, "이메일 인증된 회원 정보가 없습니다.")

        if (request.email != seller.email) {
            throw UnverifiedEmailException(400, "인증되지 않은 이메일입니다.")
        }

        seller.apply {
            nickname = request.nickname
            password = passwordEncoder.encode(request.password)
            phoneNumber = request.phoneNumber
            address = request.address
        }

        if (file != null) {
            s3Manager.uploadFile(file)
            seller.profileImage = s3Manager.getFile(file.originalFilename)
        }

        val savedSeller = sellerRepository.save(seller)

        return SellerResponse.from(savedSeller)
    }

    fun createShop(sellerId: Long, createShopRequest: CreateShopRequest, file: MultipartFile?): ShopResponse {
        if (shopRepository.existsBySellerId(sellerId)) throw CustomRuntimeException(
            409,
            "Shop with seller id $sellerId already exists"
        )
        val shop = Shop(
            sellerId = sellerId,
            name = createShopRequest.name,
            description = createShopRequest.description,
            shopImage = "",
            rate = 0.0f
        )

        if (file != null) {
            s3Manager.uploadFile(file)
            shop.shopImage = s3Manager.getFile(file.originalFilename)
        }
        val savedShop = shopRepository.save(shop)
        return ShopResponse.from(savedShop)
    }
}
