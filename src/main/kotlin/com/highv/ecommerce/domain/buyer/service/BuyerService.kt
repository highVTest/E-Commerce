package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.domain.buyer.dto.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.BuyerResponse
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
        val buyer = Buyer(
            email = request.email,
            nickname = request.nickname,
            password = passwordEncoder.encode(request.password),
            profileImage = request.profileImage,
            phoneNumber = request.phoneNumber,
            address = request.address
        )
        val savedBuyer = buyerRepository.save(buyer)
        return BuyerResponse.from(savedBuyer)
    }
}
