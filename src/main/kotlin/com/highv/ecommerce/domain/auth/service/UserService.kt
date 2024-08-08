package com.highv.ecommerce.domain.auth.service

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.BuyerLoginFailedException
import com.highv.ecommerce.common.exception.EmailAlreadyExistsException
import com.highv.ecommerce.common.exception.SellerLoginFailedException
import com.highv.ecommerce.domain.auth.dto.EmailAuthResponse
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.dto.UserRole
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.email.EmailUtils
import com.highv.ecommerce.infra.redis.RedisUtils
import com.highv.ecommerce.infra.s3.S3Manager
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
    private val s3Manager: S3Manager,

    @Value("\${spring.mail.auth-code-expiration-millis}")
    private val expirationMillis: Long
) {
    fun loginSeller(loginRequest: LoginRequest): AccessTokenResponse {

        val seller = sellerRepository.findByEmail(loginRequest.email)
        if (seller != null && passwordEncoder.matches(loginRequest.password, seller.password)) {
            val token = jwtPlugin.generateAccessToken(seller.id.toString(), seller.email, "SELLER")
            return AccessTokenResponse(token)
        }
        throw SellerLoginFailedException(message = "판매자 로그인 실패")
    }

    fun loginBuyer(loginRequest: LoginRequest): AccessTokenResponse {
        val buyer = buyerRepository.findByEmail(loginRequest.email)
        if (buyer != null && passwordEncoder.matches(loginRequest.password, buyer.password)) {
            val token = jwtPlugin.generateAccessToken(buyer.id.toString(), buyer.email, "BUYER")
            return AccessTokenResponse(token)
        }

        throw BuyerLoginFailedException(message = "구매자 로그인 실패")
    }

    fun sendMail(toEmail: String, role: UserRole): DefaultResponse {

        if (duplicateEmail(toEmail, role)) {
            throw EmailAlreadyExistsException(message = "이미 존재하는 이메일입니다.")
        }

        val title = "HighV 이메일 인증"
        val authCode = createCode()

        mailUtils.sendEmail(toEmail, title, authCode)

        redisUtils.setStringData(
            key = "${AUTH_CODE_PREFIX}${toEmail}",
            value = authCode,
            expiredTimeMillis = expirationMillis
        )

        return DefaultResponse("인증번호가 발송됐습니다.") // TODO: 배포때는 인증번호 반환 X
    }

    fun verifyCode(email: String, role: UserRole, code: String): EmailAuthResponse {

        if (duplicateEmail(email, role)) {
            throw EmailAlreadyExistsException(message = "이미 존재하는 이메일입니다.")
        }

        val redisAuthCode: String = redisUtils.getStringData("${AUTH_CODE_PREFIX}${email}")
        val authCodeIsTrue: Boolean = code == redisAuthCode

        if (authCodeIsTrue) {
            redisUtils.deleteStringData("${AUTH_CODE_PREFIX}${email}")

            return createUser(email, role)
        }



        return EmailAuthResponse(
            id = -1,
            isApproved = false,
            role = role,
        )
    }

    private fun createUser(email: String, role: UserRole): EmailAuthResponse {
        val userId: Long
        if (role == UserRole.BUYER) {
            val buyer = Buyer(
                nickname = "",
                password = "",
                email = email,
                profileImage = "",
                phoneNumber = "",
                address = ""
            )

            userId = buyerRepository.saveAndFlush(buyer).id!!
        } else {
            val seller = Seller(
                nickname = "",
                password = "",
                email = email,
                profileImage = "",
                phoneNumber = "",
                address = "",
                activeStatus = Seller.ActiveStatus.PENDING
            )

            userId = sellerRepository.saveAndFlush(seller).id!!
        }

        return EmailAuthResponse(
            id = userId,
            isApproved = true,
            role = role
        )
    }

    private fun createCode(): String {
        val length = 6
        val charSet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charSet.random() }
            .joinToString("")
    }

    private fun duplicateEmail(email: String, role: UserRole): Boolean {

        if (role == UserRole.BUYER) {
            if (buyerRepository.existsByEmail(email)) {
                return true
            }
        } else if (role == UserRole.SELLER) {
            if (sellerRepository.existsByEmail(email)) {
                return true
            }
        }

        return false
    }

    companion object {
        const val AUTH_CODE_PREFIX = "AuthCode"
    }
}