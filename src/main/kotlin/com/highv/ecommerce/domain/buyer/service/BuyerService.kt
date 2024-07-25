package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
import com.highv.ecommerce.domain.buyer.dto.response.BuyerResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.s3.config.S3Manager
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
        val buyer: Buyer = buyerRepository.findByIdOrNull(request.id) ?: throw RuntimeException("이메일 인증된 회원 정보가 없습니다.")

        if (request.email != buyer.email) {
            throw RuntimeException("인증되지 않은 이메일입니다.")
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

    @Transactional
    fun changeProfileImage(userId: Long, file: MultipartFile?) {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")

        if (file != null) {
            s3Manager.uploadFile(file)
            buyer.profileImage = s3Manager.getFile(file.originalFilename)
        } else {
            buyer.profileImage = ""
        }

        buyerRepository.save(buyer)
    }

    @Transactional
    fun changeProfile(request: UpdateBuyerProfileRequest, userId: Long): BuyerResponse {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")

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

    // @Transactional
    // // 주문 내역에서 상품(상품이 포함된 주문목록 전체) 교환 및 환불 신청하기
    // fun updateStatus(buyerId: Long, orderId: Long, request: BuyerOrderStatusUpdateRequest): BuyerOrderResponse {
    //     /*
    //     * 1. orderId와 userId로 주문 정보 목록 불러오기
    //     * 2. request의 상태로 상태 변경 후 이유 넣기
    //     * 3. productsOrder의 status를 Pending으로 변경하기
    //     * 4. 변경된 내용 반환
    //     * */
    //
    //     val productsOrder: OrderMaster =
    //         orderMasterRepository.findByIdOrNull(orderId) ?: throw RuntimeException("수정할 주문 내역이 없습니다.")
    //
    //     val orderStatuses: List<OrderDetails> =
    //         //TODO("수정 필요")
    //         orderDetailsRepository.findAllByBuyerId(buyerId)
    //
    //     val orderPendingReason: ComplainStatus = when (request.status) {
    //         ComplainType.EXCHANGE -> ComplainStatus.EXCHANGE_REQUESTED
    //         ComplainType.REFUND -> ComplainStatus.REFUND_REQUESTED
    //     }
    //     val now: LocalDateTime = LocalDateTime.now()
    //
    //     // 이것보단 한방 쿼리가 좋을 거라 생각 됨
    //     orderStatuses.forEach {
    //         it.complainStatus = orderPendingReason
    //         it.buyerDescription = request.reason
    //         it.buyerDateTime = now
    //     }
    //
    //     // 위에랑 어짜피 같은 것임 지워도 되지 않을까?
    //     val savedOrderStatuses = orderDetailsRepository.saveAll(orderStatuses)
    //     val savedProductsOrder = orderMasterRepository.save(productsOrder)
    //
    //     // 수정 필요
    //     // val buyerHistoryProductResponses: List<BuyerHistoryProductResponse> = savedOrderStatuses.map {
    //     //     BuyerHistoryProductResponse.from(
    //     //         cart = it.orderPendingReason = orderPendingReason,
    //     //         it.id!!
    //     //     )
    //     // }
    //     //
    //     // return BuyerOrderResponse(
    //     //     productsOrderId = savedProductsOrder.id!!,
    //     //     orderRegisterDate = savedProductsOrder.regDateTime,
    //     //     orderStatus = OrderStatus.PENDING,
    //     //     savedOrderStatuses.map { Order }
    //     // )
    //     TODO()
    // }
}
