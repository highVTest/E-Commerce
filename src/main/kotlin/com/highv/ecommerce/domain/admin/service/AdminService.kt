package com.highv.ecommerce.domain.admin.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.admin.entity.BlackList
import com.highv.ecommerce.domain.admin.repository.BlackListRepository
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
            ?: throw CustomRuntimeException(404, "Seller id $sellerId not found")

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
            ?: throw CustomRuntimeException(404, "Product id $productId not found")

        // 상품에서 판매자 정보를 가져옵니다.
        val seller = product.shop.sellerId
            ?.let { sellerRepository.findByIdOrNull(it) }
            ?: throw CustomRuntimeException(404, "Seller not found for product id $productId")

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
            ?: throw CustomRuntimeException(404, "Buyer id $buyerId not found")
        buyerRepository.save(buyer)
        return DefaultResponse("구매자 제재 완료")
    }*/

    // 블랙리스트 조회 로직 구현
    fun getBlackLists(): List<BlackListResponse> {
        return blackListRepository.findAll().map {
            BlackListResponse(it.nickname, it.email,it.sanctionsCount,it.isSanctioned)
        }
    }

    // 블랙리스트 단건 조회 로직 구현
    fun getBlackList(blackListId: Long): BlackListResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw CustomRuntimeException(404, "블랙리스트가 존재하지 않습니다.")
        return BlackListResponse(blackList.nickname, blackList.email,blackList.sanctionsCount,blackList.isSanctioned)
    }

    // 블랙리스트 삭제 로직 구현
    fun deleteBlackList(blackListId: Long): DefaultResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw CustomRuntimeException(404, "블랙리스트 id $blackListId 존재하지 않습니다.")
        blackListRepository.delete(blackList)
        return DefaultResponse("블랙리스트 삭제 완료")
    }
    /* 기능 추가할 부분이 많아서 V2에서 추가할 예정
        // 판매자 탈퇴 대기 회원 승인
        @Transactional
        fun approveSellerResignation(sellerId: Long): DefaultResponse {
            val seller = sellerRepository.findByIdOrNull(sellerId)
                ?: throw CustomRuntimeException(404, "Seller id $sellerId not found")
            sellerRepository.delete(seller)
            return DefaultResponse("판매자 탈퇴 승인 완료")
        }

        // 판매자 승인 대기 회원 승격
        Seller Entity에 status가 없어서 주석처리
        @Transactional
        fun promotePendingSeller(sellerId: Long): DefaultResponse {
            val seller = sellerRepository.findByIdOrNull(sellerId)
                ?: throw CustomRuntimeException(404, "Seller id $sellerId not found")
            seller.status = "APPROVED" //판매자의 상태 APPROVED로 변경한다고 가정
            sellerRepository.save(seller)
            return DefaultResponse("판매자 승격 완료")
        }
        */
}
