package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderMasterService(
    private val orderMasterRepository: OrderMasterRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val itemCartRepository: ItemCartRepository,
    private val buyerRepository: BuyerRepository,
    ){

    @Transactional
    fun requestPayment(buyerId: Long, couponIdList: List<Long>, cartId: Long): DefaultResponse {

        val cart = itemCartRepository.findAllByBuyerId(buyerId)

        val totalPrice = orderMasterRepository.discountTotalPriceList(buyerId, couponIdList)

        val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw RuntimeException("구매자 정보가 존재 하지 않습니다")

        //TODO(만약에 CartItem 에 ProductId 가 Coupon 의 ProductId와 일치할 경우 CartItem 의 가격을 임시로 업데이트)

        val orderMaster = orderMasterRepository.saveAndFlush(
            OrderMaster(
                regDateTime = LocalDateTime.now(),
            )
        )

        orderDetailsRepository.saveAll(
            cart.map {
                OrderDetails(
                    orderStatus = OrderStatus.ORDERED,
                    complainStatus = ComplainStatus.NONE,
                    buyer = buyer,
                    product = it.product,
                    orderMaster = orderMaster,
                    productQuantity = 1,
                    shopId = it.product.shop.id!!
                )
            }

        )

        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${orderMaster.id}")
    }

}