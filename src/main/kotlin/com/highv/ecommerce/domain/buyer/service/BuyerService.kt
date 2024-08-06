package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.BuyerNotFoundException
import com.highv.ecommerce.common.exception.DuplicatePasswordException
import com.highv.ecommerce.common.exception.EmailNotVerifiedException
import com.highv.ecommerce.common.exception.PasswordMismatchException
import com.highv.ecommerce.common.exception.SocialLoginException
import com.highv.ecommerce.common.exception.UnauthorizedEmailException
import com.highv.ecommerce.domain.auth.oauth.naver.dto.OAuthLoginUserInfo
import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
import com.highv.ecommerce.domain.buyer.dto.response.BuyerResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.infra.s3.S3Manager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class BuyerService(
    private val buyerRepository: BuyerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val s3Manager: S3Manager
) {

    @Transactional
    fun signUp(request: CreateBuyerRequest, file: MultipartFile?): BuyerResponse {
        val buyer: Buyer =
            buyerRepository.findByIdOrNull(request.id) ?: throw EmailNotVerifiedException(404, "이메일 인증된 회원 정보가 없습니다.")

        if (request.email != buyer.email) {
            throw UnauthorizedEmailException(400, "인증되지 않은 이메일입니다.")
        }

        buyer.apply {
            nickname = request.nickname
            password = passwordEncoder.encode(request.password)
            phoneNumber = request.phoneNumber
            address = request.address
        }

        if (file != null) {
            s3Manager.uploadFile(file) // S3Manager를 통해 파일 업로드
            buyer.profileImage = s3Manager.getFile(file.originalFilename) // Buyer 객체에 프로필 이미지 URL 저장
        }
        val savedBuyer = buyerRepository.save(buyer)

        return BuyerResponse.from(savedBuyer)
    }

    fun getMyProfile(buyerId: Long): BuyerResponse {
        val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw BuyerNotFoundException(404, "구매자 정보가 없습니다.")

        return BuyerResponse.from(buyer)
    }

    @Transactional
    fun changePassword(request: UpdateBuyerPasswordRequest, userId: Long): DefaultResponse {
        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw BuyerNotFoundException(404, "구매자 회원 정보가 존재하지 않습니다.")

        if (buyer.providerName != null) {
            throw SocialLoginException(400, "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.")
        }

        if (!passwordEncoder.matches(request.currentPassword, buyer.password)) {
            throw PasswordMismatchException(400, "현재 비밀번호가 일치하지 않습니다.")
        }

        if (request.newPassword != request.confirmNewPassword) {
            throw PasswordMismatchException(400, "변경할 비밀번호와 확인 비밀번호가 다릅니다.")
        }

        if (passwordEncoder.matches(request.newPassword, buyer.password)) {
            throw DuplicatePasswordException(400, "현재 비밀번호와 수정할 비밀번호가 같습니다.")
        }

        buyer.password = passwordEncoder.encode(request.newPassword)
        return DefaultResponse("비밀번호가 변경되었습니다.")
    }

    @Transactional
    fun changeProfileImage(userId: Long, file: MultipartFile?): DefaultResponse {
        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw BuyerNotFoundException(404, "구매자 회원 정보가 존재하지 않습니다.")

        if (file != null) {
            s3Manager.uploadFile(file)
            buyer.profileImage = s3Manager.getFile(file.originalFilename)
        } else {
            buyer.profileImage = ""
        }

        buyerRepository.save(buyer)
        return DefaultResponse("프로필 이미지가 변경되었습니다.")
    }

    @Transactional
    fun changeProfile(request: UpdateBuyerProfileRequest, userId: Long): BuyerResponse {
        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw BuyerNotFoundException(404, "사용자가 존재하지 않습니다.")

        if (buyer.providerName != null) {
            buyer.address = request.address
            buyer.phoneNumber = request.phoneNumber
        } else {
            buyer.nickname = request.nickname
            buyer.address = request.address
            buyer.phoneNumber = request.phoneNumber
        }

        val saveBuyer = buyerRepository.save(buyer)

        return BuyerResponse.from(saveBuyer)
    }

    fun registerIfAbsent(userInfo: OAuthLoginUserInfo): Buyer {
        return buyerRepository.findByProviderNameAndProviderId(userInfo.provider.toString(), userInfo.id)
            ?: buyerRepository.save(
                Buyer(
                    email = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
                    password = "null", // 소셜 로그인 한 사람은 업데이트 못하게 하기
                    phoneNumber = "null", // 소셜 로그인 한 사람은 업데이트 하기
                    address = "null", // 소셜 로그인 한 사람은 업데이트 하기
                    providerName = userInfo.provider.toString(),
                    providerId = userInfo.id,
                    nickname = userInfo.nickname,
                    profileImage = userInfo.profileImage
                )
            )
    }
}
