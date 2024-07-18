package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.domain.buyer.dto.BuyerResponse
import com.highv.ecommerce.domain.buyer.dto.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class BuyerService(
    private val buyerRepository: BuyerRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun signUp(request: CreateBuyerRequest): BuyerResponse {

        if (buyerRepository.existsByEmail(request.email)) {
            throw RuntimeException("이미 존재하는 이메일입니다. 가입할 수 없습니다.")
        }

        val buyer = Buyer(
            email = request.email,
            nickname = request.nickname,
            password = passwordEncoder.encode(request.password),
            profileImage = request.profileImage,
            phoneNumber = request.phoneNumber,
            address = request.address,
            providerName = null,
            providerId = null
        )

        val savedBuyer = buyerRepository.save(buyer)

        return BuyerResponse.from(savedBuyer)
    }
}
