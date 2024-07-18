package com.highv.ecommerce.domain.seller.service

import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun signUp(request: CreateSellerRequest): SellerResponse {

        if (sellerRepository.existsByEmail(request.email)) {
            throw RuntimeException("이미 존재하는 이메일입니다. 가입할 수 없습니다.")
        }

        val seller = Seller(
            email = request.email,
            nickname = request.nickname,
            password = passwordEncoder.encode(request.password),
            profileImage = request.profileImage,
            phoneNumber = request.phoneNumber,
            address = request.address
        )

        val savedSeller = sellerRepository.save(seller)

        return SellerResponse.from(savedSeller)
    }
}
