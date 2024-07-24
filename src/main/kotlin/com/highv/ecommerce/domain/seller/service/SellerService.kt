package com.highv.ecommerce.domain.seller.service

import com.highv.ecommerce.domain.seller.dto.CreateSellerRequest
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.s3.config.FileUtil
import com.highv.ecommerce.s3.config.S3Manager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val s3Manager: S3Manager,

) {

    fun signUp(request: CreateSellerRequest, multipartFile: MultipartFile): SellerResponse {

        if (sellerRepository.existsByEmail(request.email)) {
            throw RuntimeException("이미 존재하는 이메일입니다. 가입할 수 없습니다.")
        }
        s3Manager.uploadFile(multipartFile) // S3Manager를 통해 파일 업로드

        val seller = Seller(
            email = request.email,
            nickname = request.nickname,
            password = passwordEncoder.encode(request.password),
            profileImage = s3Manager.getFile(multipartFile.originalFilename),
            phoneNumber = request.phoneNumber,
            address = request.address
        )

        val savedSeller = sellerRepository.save(seller)

        return SellerResponse.from(savedSeller)
    }
}
