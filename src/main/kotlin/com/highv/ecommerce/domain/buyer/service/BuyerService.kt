package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.domain.buyer.dto.request.BuyerOrderStatusUpdateRequest
import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerImageRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
import com.highv.ecommerce.domain.buyer.dto.response.BuyerHistoryProductResponse
import com.highv.ecommerce.domain.buyer.dto.response.BuyerOrderResponse
import com.highv.ecommerce.domain.buyer.dto.response.BuyerResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_details.enumClass.ComplainType
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BuyerService(
    private val buyerRepository: BuyerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val productsOrderRepository: OrderMasterRepository
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

    @Transactional
    fun changeProfileImage(request: UpdateBuyerImageRequest, userId: Long) {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")

        buyer.profileImage = request.imageUrl

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

    // 추후 리팩토링 때 내부 로직에서 orderService의 로직 이용 가능한 것 있으면 사용
    fun getOrders(buyerId: Long): List<BuyerOrderResponse> {
        /* // 주문 내역 전체 불러오기
        * 1. buyerId를 이용해서 주문 내역 전부 가져오기
        * 2. 주문 내역에 있는 order_id를 이용해서 status와 장바구니 내역 가져오기
        * 3. 장바구니 내역과 잘 조합해서 반환하기
        * */

        // val buyerHistories: List<BuyerHistory> = buyerHistoryRepository.findAllByBuyerId(buyerId)

        val orderStatuses: List<OrderDetails> = orderDetailsRepository.findAllByBuyerId(buyerId)

        // val orderGroups: MutableMap<Long, MutableList<OrderStatus>> = mutableMapOf()
        //
        // orderStatuses.forEach {
        //
        //     if (!orderGroups.containsKey(it.productsOrder.id!!)) {
        //         orderGroups[it.productsOrder.id] = mutableListOf<OrderStatus>()
        //     }
        //     orderGroups[it.productsOrder.id]!!.add(it)
        // }

        val orderMap: MutableMap<Long, OrderMaster> = mutableMapOf()
        orderStatuses.forEach {
            if (!orderMap.containsKey(it.orderMaster.id!!)) {
                orderMap[it.orderMaster.id] = it.orderMaster
            }
        }

        val orderGroups: MutableMap<Long, MutableList<BuyerHistoryProductResponse>> = mutableMapOf()

        orderStatuses.forEach {

            if (!orderGroups.containsKey(it.orderMaster.id!!)) {
                orderGroups[it.orderMaster.id] = mutableListOf<BuyerHistoryProductResponse>()
            }
            orderGroups[it.orderMaster.id]!!.add(
                BuyerHistoryProductResponse.from(
//                    cart = it.itemCart, // 수정 필요
                    complainStatus = it.complainStatus,
                    orderStatusId = it.id!!
                )
            )
        }

        val buyerOrderResponse: List<BuyerOrderResponse> = orderMap.map {
            BuyerOrderResponse.from(
                productsOrder = it.value,
                products = orderGroups[it.key]!!,
            )
        }

        return buyerOrderResponse.sortedByDescending { it.orderRegisterDate }
    }

    @Transactional
    // 주문 내역에서 상품(상품이 포함된 주문목록 전체) 교환 및 환불 신청하기
    fun updateStatus(buyerId: Long, orderId: Long, request: BuyerOrderStatusUpdateRequest): BuyerOrderResponse {
        /*
        * 1. orderId와 userId로 주문 정보 목록 불러오기
        * 2. request의 상태로 상태 변경 후 이유 넣기
        * 3. productsOrder의 status를 Pending으로 변경하기
        * 4. 변경된 내용 반환
        * */

        val productsOrder: OrderMaster =
            productsOrderRepository.findByIdOrNull(orderId) ?: throw RuntimeException("수정할 주문 내역이 없습니다.")

        val orderStatuses: List<OrderDetails> =
            //TODO("수정 필요")
            orderDetailsRepository.findAllByBuyerId(buyerId)

        val orderPendingReason: ComplainStatus = when (request.status) {
            ComplainType.EXCHANGE -> ComplainStatus.EXCHANGE_REQUESTED
            ComplainType.REFUND -> ComplainStatus.REFUND_REQUESTED
        }
        val now: LocalDateTime = LocalDateTime.now()

        // 이것보단 한방 쿼리가 좋을 거라 생각 됨
        orderStatuses.forEach {
            it.complainStatus = orderPendingReason
            it.buyerDescription = request.reason
            it.buyerDateTime = now
        }

        // 위에랑 어짜피 같은 것임 지워도 되지 않을까?
        val savedOrderStatuses = orderDetailsRepository.saveAll(orderStatuses)
        val savedProductsOrder = productsOrderRepository.save(productsOrder)

        // 수정 필요
//        val buyerHistoryProductResponses: List<BuyerHistoryProductResponse> = savedOrderStatuses.map {
//            BuyerHistoryProductResponse.from(
//                cart = it.itemCart,
//                orderPendingReason = orderPendingReason,
//                it.id!!
//            )
//        }

        return BuyerOrderResponse(
            productsOrderId = savedProductsOrder.id!!,
            orderRegisterDate = savedProductsOrder.regDateTime,
            orderStatus = OrderStatus.PENDING,
//            orderStatus = savedProductsOrder.statusCode,
        )
    }
}
