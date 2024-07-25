package com.highv.ecommerce.domain.backoffice.admin.service

import com.highv.ecommerce.domain.backoffice.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.backoffice.admin.repository.BlackListRepository
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val sellerRepository: SellerRepository,
    private val productRepository: ProductRepository,
    // private val buyerRepository: BuyerRepository,
    private val blackListRepository: BlackListRepository
) {
    //판매자 제재 로직 구현
    fun sanctionSeller(sellerId: Long) {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw RuntimeException("Seller id $sellerId not found")
        sellerRepository.save(seller)
    }

    // 상품 제재 로직 구현
    fun sanctionProduct(productId: Long) {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product id $productId not found")
        productRepository.save(product)
    }

/*    //구매자 제재 로직 구현 (미구현)
    fun sanctionBuyer(buyerId: Long) {
        val buyer = buyerRepository.findByIdOrNull(buyerId)
            ?: throw RuntimeException("Buyer id $buyerId not found")
        buyerRepository.save(buyer)
    }*/

    //블랙리스트 단건 조회 로직 구현
    fun getBlackList(blackListId: Long): BlackListResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw RuntimeException("BlackList not found")
        return BlackListResponse(blackList.nickname, blackList.email)
    }

    //블랙리스트 삭제 로직 구현
    fun deleteBlackList(blackListId: Long) {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw RuntimeException("BlackList id $blackListId not found")
        blackListRepository.delete(blackList)
    }

}
