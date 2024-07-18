package com.highv.ecommerce.domain.login.service

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.login.dto.LoginRequest
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val buyerRepository: BuyerRepository,
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin

) {
    fun login(loginRequest: LoginRequest): AccessTokenResponse {

        if (loginRequest.role == "BUYER") {
            val buyer = buyerRepository.findByEmail(loginRequest.email)
            if (buyer != null && passwordEncoder.matches(loginRequest.password, buyer.password)) {
                val token = jwtPlugin.generateAccessToken(buyer.id.toString(), buyer.email, "BUYER")
                return AccessTokenResponse(token)
            }
        } else if (loginRequest.role == "SELLER") {
            val seller = sellerRepository.findByEmail(loginRequest.email)
            if (seller != null && passwordEncoder.matches(loginRequest.password, seller.password)) {
                val token = jwtPlugin.generateAccessToken(seller.id.toString(), seller.email, "SELLER")
                return AccessTokenResponse(token)
            }
        }

        throw IllegalArgumentException("Invalid email or password")
    }
}