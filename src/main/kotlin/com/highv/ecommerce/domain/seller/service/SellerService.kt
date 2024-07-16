package com.highv.ecommerce.domain.seller.service

import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun signUp(request: CreateSellerRequest): SellerResponse {
        val seller = Seller(
            email = request.email,
            nickname = request.nickname,
            password = passwordEncoder.encode(request.password),
            profileImage = request.profileImage ?: "",
            phoneNumber = request.phoneNumber,
            address = request.address
        )
        val savedSeller = sellerRepository.save(seller)
        return SellerResponse.from(savedSeller)
    }
}
