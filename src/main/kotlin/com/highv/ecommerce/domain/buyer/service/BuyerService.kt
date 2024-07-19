package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.response.BuyerResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    fun changePassword(request: UpdateBuyerPasswordRequest, userId: Long) {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")

        if (buyer.providerName != null) {
            throw RuntimeException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.")
        }

        if (!passwordEncoder.matches(request.currentPassword, buyer.password)) {
            throw RuntimeException("비밀번호가 일치하지 않습니다.")
        }

        if (request.newPassword != request.confirmNewPassword) {
            throw RuntimeException("변경할 비밀번호와 확인 비밀번호가 다릅니다.")
        }

        if (passwordEncoder.matches(request.newPassword, buyer.password)) {
            throw RuntimeException("현재 비밀번호와 수정할 비밀번호가 같습니다.")
        }

        buyer.password = passwordEncoder.encode(request.newPassword)
    }
}
