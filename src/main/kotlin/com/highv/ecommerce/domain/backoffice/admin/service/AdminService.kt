package com.highv.ecommerce.domain.backoffice.admin.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.backoffice.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.backoffice.admin.entity.BlackList
import com.highv.ecommerce.domain.backoffice.admin.repository.BlackListRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val sellerRepository: SellerRepository,
    private val productRepository: ProductRepository,
    private val blackListRepository: BlackListRepository
) {
    // 판매자 제재 로직 구현
    @Transactional
    fun sanctionSeller(sellerId: Long): DefaultResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw RuntimeException("Seller id $sellerId not found")

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

        return DefaultResponse("판매자 제재 완료")
    }

    // 상품 제재 로직 구현
    @Transactional
    fun sanctionProduct(productId: Long): DefaultResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product id $productId not found")

        // 상품에서 판매자 정보를 가져옵니다.
        val seller = product.shop.sellerId
            ?.let { sellerRepository.findByIdOrNull(it) }
            ?: throw RuntimeException("Seller not found for product id $productId")

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

    /*    //구매자 제재 로직 구현 (미구현)
    fun sanctionBuyer(buyerId: Long): DefaultResponse {
        val buyer = buyerRepository.findByIdOrNull(buyerId)
            ?: throw RuntimeException("Buyer id $buyerId not found")
        buyerRepository.save(buyer)
        return DefaultResponse("구매자 제재 완료")
    }*/

    // 블랙리스트 조회 로직 구현
    fun getBlackLists(): List<BlackListResponse> {
        return blackListRepository.findAll().map {
            BlackListResponse(it.nickname, it.email)
        }
    }

    // 블랙리스트 단건 조회 로직 구현
    fun getBlackList(blackListId: Long): BlackListResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw RuntimeException("BlackList not found")
        return BlackListResponse(blackList.nickname, blackList.email)
    }

    // 블랙리스트 삭제 로직 구현
    fun deleteBlackList(blackListId: Long): DefaultResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw RuntimeException("BlackList id $blackListId not found")
        blackListRepository.delete(blackList)
        return DefaultResponse("블랙리스트 삭제 완료")
    }

    // 판매자 탈퇴 대기 회원 승인
    @Transactional
    fun approveSellerResignation(sellerId: Long): DefaultResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw RuntimeException("Seller id $sellerId not found")
        sellerRepository.delete(seller)
        return DefaultResponse("판매자 탈퇴 승인 완료")
    }

    // // 판매자 승인 대기 회원 승격
    // Seller Entity에 status가 없어서 주석처리
    // @Transactional
    // fun promotePendingSeller(sellerId: Long): DefaultResponse {
    //     val seller = sellerRepository.findByIdOrNull(sellerId)
    //         ?: throw RuntimeException("Seller id $sellerId not found")
    //     seller.status = "APPROVED" //판매자의 상태 APPROVED로 변경한다고 가정
    //     sellerRepository.save(seller)
    //     return DefaultResponse("판매자 승격 완료")
    // }
}
