package com.highv.ecommerce.domain.backoffice.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.exception.DuplicatePasswordException
import com.highv.ecommerce.common.exception.PasswordMismatchException
import com.highv.ecommerce.common.exception.SellerNotFoundException
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateImageRequest
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
        }
        val updatedShop = shopRepository.save(shop)
        return ShopResponse.from(updatedShop)
    }

    fun updateSellerInfo(sellerId: Long, updateSellerRequest: UpdateSellerRequest): SellerResponse {
        val seller =
            sellerRepository.findByIdOrNull(sellerId) ?: throw SellerNotFoundException(message = "Seller not found")
        seller.apply {
            address = updateSellerRequest.address
            nickname = updateSellerRequest.nickname
            phoneNumber = updateSellerRequest.phoneNumber
        }
        val updateSellerInfo = sellerRepository.save(seller)
        return SellerResponse.from(updateSellerInfo)
    }

    fun changePassword(sellerId: Long, request: UpdatePasswordRequest): DefaultResponse {
        val seller =
            sellerRepository.findByIdOrNull(sellerId) ?: throw SellerNotFoundException(message = "Seller not found")

        if (!passwordEncoder.matches(request.currentPassword, seller.password)) {
            throw PasswordMismatchException(400, "비밀번호가 일치하지 않습니다.")
        } else if (passwordEncoder.matches(request.newPassword, seller.password)) {
            throw DuplicatePasswordException(400, "현재 비밀번호와 수정할 비밀번호가 같습니다.")
        } else if (request.newPassword != request.confirmNewPassword) {
            throw PasswordMismatchException(400, "변경할 비밀번호와 확인 비밀번호가 다릅니다.")
        }

        seller.password = passwordEncoder.encode(request.newPassword)

        sellerRepository.save(seller)

        return DefaultResponse("비밀번호가 변경되었습니다.")
    }

    fun getSellerInfo(sellerId: Long): SellerResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId) ?: throw CustomRuntimeException(404, "Seller not found")
        return SellerResponse.from(seller)
    }

    fun getShopInfo(sellerId: Long): ShopResponse {
        val shop = shopRepository.findShopBySellerId(sellerId)
        return ShopResponse.from(shop)
    }

    fun changeSellerImage(sellerId: Long, request: UpdateImageRequest): DefaultResponse {
        val seller =
            sellerRepository.findByIdOrNull(sellerId) ?: throw SellerNotFoundException(message = "Seller not found")

        seller.profileImage = request.imageUrl

        sellerRepository.save(seller)

        if (request.imageUrl.isEmpty()) {
            return DefaultResponse("이미지를 삭제했습니다.")
        }

        return DefaultResponse("이미지를 변경했습니다.")
    }

    fun changeShopImage(sellerId: Long, request: UpdateImageRequest): DefaultResponse {
        val shop = shopRepository.findShopBySellerId(sellerId)

        shop.shopImage = request.imageUrl

        shopRepository.save(shop)

        if (request.imageUrl.isEmpty()) {
            return DefaultResponse("이미지를 삭제했습니다.")
        }

        return DefaultResponse("이미지를 변경했습니다.")
    }
}