package com.highv.ecommerce.domain.admin.service

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.*
import com.highv.ecommerce.domain.admin.dto.AdminBySellerResponse
import com.highv.ecommerce.domain.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.admin.entity.BlackList
import com.highv.ecommerce.domain.admin.repository.AdminRepository
import com.highv.ecommerce.domain.admin.repository.BlackListRepository
import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.dto.ActiveStatus
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.domain.seller.shop.repository.ShopRepository
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminService(
    private val sellerRepository: SellerRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository,
    private val blackListRepository: BlackListRepository,
    private val adminRepository: AdminRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin,

    ) {
    fun loginAdmin(loginRequest: LoginRequest): AccessTokenResponse {

        val admin = adminRepository.findByEmail(loginRequest.email)
        if (admin != null && passwordEncoder.matches(loginRequest.password, admin.password)) {
            val token = jwtPlugin.generateAccessToken(admin.id.toString(), admin.email, "ADMIN")
            return AccessTokenResponse(token)
        }
        throw AdminLoginFailedException(message = "관리자 로그인 실패")
    }

    // 판매자 제재 로직 구현
    @Transactional
    fun sanctionSeller(sellerId: Long): DefaultResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw SellerNotFoundException(message = "Seller id $sellerId not found")

        val existingBlackList = blackListRepository.findByEmail(seller.email)

        if (existingBlackList != null) {
            // 제재 횟수를 증가시킵니다.
            existingBlackList.sanctionsCount += 1
            // 제재 횟수가 5 이상이면 제재 상태로 설정합니다.
            if (existingBlackList.sanctionsCount >= 5) {
                existingBlackList.isSanctioned = true
                seller.activeStatus = ActiveStatus.SANCTIONED
            }
            blackListRepository.save(existingBlackList)
        } else {
            // 블랙리스트에 판매자가 없을 경우 새로운 블랙리스트 항목을 생성합니다.
            val blackList = BlackList(
                nickname = seller.nickname,
                email = seller.email,
                sanctionsCount = 1
            )
            blackListRepository.save(blackList)
        }

        return DefaultResponse("판매자 제재 완료")
    }

    // 상품 제재 로직 구현
    @Transactional
    fun sanctionProduct(productId: Long): DefaultResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductNotFoundException(message = "Product id $productId not found")

        // 상품에서 판매자 정보를 가져옵니다.
        val seller = product.shop.sellerId
            .let { sellerRepository.findByIdOrNull(it) }
            ?: throw SellerNotFoundException(message = "Seller not found for product id $productId")

        // 판매자의 이메일로 블랙리스트에서 검색합니다.
        val existingBlackList = blackListRepository.findByEmail(seller.email)

        if (existingBlackList != null) {
            // 제재 횟수를 증가시킵니다.
            existingBlackList.sanctionsCount += 1
            // 제재 횟수가 5 이상이면 제재 상태로 설정합니다.
            if (existingBlackList.sanctionsCount >= 5) {
                existingBlackList.isSanctioned = true
            }
            blackListRepository.save(existingBlackList)
        } else {
            // 블랙리스트에 판매자가 없을 경우 새로운 블랙리스트 항목을 생성합니다.
            val blackList = BlackList(
                nickname = seller.nickname,
                email = seller.email,
                sanctionsCount = 1
            )
            blackListRepository.save(blackList)
        }

        return DefaultResponse("상품 제재 완료")
    }

    // 블랙리스트 조회 로직 구현
    fun getBlackLists(): List<BlackListResponse> {
        return blackListRepository.findAll().map {
            BlackListResponse(it.id!!, it.nickname, it.email, it.sanctionsCount, it.isSanctioned)
        }
    }

    // 블랙리스트 단건 조회 로직 구현
    fun getBlackList(blackListId: Long): BlackListResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw BlackListNotFoundException(message = "블랙리스트가 존재하지 않습니다.")
        return BlackListResponse(blackListId, blackList.nickname, blackList.email, blackList.sanctionsCount, blackList.isSanctioned)
    }

    // 블랙리스트 삭제 로직 구현
    fun deleteBlackList(blackListId: Long): DefaultResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw BlackListNotFoundException(message = "블랙리스트 id $blackListId 존재하지 않습니다.")
        blackListRepository.delete(blackList)
        return DefaultResponse("블랙리스트 삭제 완료")
    }

    // 판매자 탈퇴 대기 회원 승인 로직 구현
    @Transactional
    fun approveSellerResignation(sellerId: Long): DefaultResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw SellerNotFoundException(message = "판매자 id $sellerId not found")

        // 판매자 상태를 탈퇴 승인으로 변경합니다.
        seller.activeStatus = ActiveStatus.RESIGNED

        // 해당 판매자의 Shop을 찾습니다.
        val shop = shopRepository.findBySellerId(sellerId)

        // Shop에 속한 모든 Product를 삭제(소프트 삭제)합니다.
        val products = productRepository.findAllByShopId(shop.id!!)
        products.forEach { product ->
            product.isDeleted = true
            product.deletedAt = LocalDateTime.now()
            productRepository.save(product)
        }

        return DefaultResponse("판매자 탈퇴 승인 및 상품 삭제 완료")
    }

    // 판매자 승인 대기 회원 승격 로직 구현
    @Transactional
    fun promotePendingSeller(sellerId: Long): DefaultResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw SellerNotFoundException(message = "판매자 id $sellerId not found")

        // 판매자 상태를 승인 완료로 변경합니다.
        seller.activeStatus = ActiveStatus.APPROVED

        return DefaultResponse("판매자 승인 완료")
    }

    // 판매자 전체 조회 로직 구현
    fun getSellerLists(): List<SellerResponse> {
        val sellers = sellerRepository.findAll()
        return sellers.map { seller ->
            SellerResponse.from(seller)
        }
    }

   fun getSellerBySellerId(sellerId: Long): AdminBySellerResponse {
       val seller = sellerRepository.findByIdOrNull(sellerId) ?: throw SellerNotFoundException(message = "판매자 id $sellerId not found")
       val shop = shopRepository.findShopBySellerId(sellerId)

       return AdminBySellerResponse.from(shop, seller)
   }
}
