package com.highv.ecommerce.domain.auth.service

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.domain.auth.controller.UserRole
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.email.EmailUtils
import com.highv.ecommerce.infra.redis.RedisUtils
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val buyerRepository: BuyerRepository,
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin,
    private val redisUtils: RedisUtils,
    private val mailUtils: EmailUtils,

    @Value("\${spring.mail.auth-code-expiration-millis}")
    private val expirationMillis: Long
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

    fun sendMail(toEmail: String, role: UserRole): String {

        duplicateEmail(toEmail, role)

        val title = "HighV 이메일 인증"
        val authCode = createCode()

        mailUtils.sendEmail(toEmail, title, authCode)

        redisUtils.setStringData(
            key = "${AUTH_CODE_PREFIX}${toEmail}",
            value = authCode,
            expiredTimeMinutes = expirationMillis
        )

        return authCode
    }

    fun verifyCode(email: String, code: String, role: UserRole): Boolean {

        duplicateEmail(email, role)

        val redisAuthCode: String = redisUtils.getStringData("${AUTH_CODE_PREFIX}${email}")
        val authCodeIsTrue: Boolean = code == redisAuthCode

        if (authCodeIsTrue) {
            redisUtils.deleteStringData("${AUTH_CODE_PREFIX}${email}")
        }

        return authCodeIsTrue
    }

    private fun createCode(): String {
        val length = 6
        val charSet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charSet.random() }
            .joinToString("")
    }

    private fun duplicateEmail(email: String, role: UserRole) {

        if (role == UserRole.BUYER && buyerRepository.existsByEmail(email)) {
            throw RuntimeException("이미 존재하는 이메일입니다.")
        }

        if (role == UserRole.SELLER && sellerRepository.existsByEmail(email)) {
            throw RuntimeException("이미 존재하는 이메일입니다.")
        }
    }

    companion object {
        const val AUTH_CODE_PREFIX = "AuthCode"
    }
}