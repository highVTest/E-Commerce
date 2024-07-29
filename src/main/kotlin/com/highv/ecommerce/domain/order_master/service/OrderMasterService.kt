package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.dto.PaymentRequest
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
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
    private val couponToBuyerRepository: CouponToBuyerRepository,
) {

    @Transactional
    fun requestPayment(buyerId: Long, paymentRequest: PaymentRequest): DefaultResponse {

        if (paymentRequest.cartIdList.isEmpty()) throw RuntimeException("장바구니 에서 아이템 목록을 선택해 주세요")

        val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw RuntimeException("구매자 정보가 존재 하지 않습니다")

        val cart = itemCartRepository.findAllByIdAndBuyerId(paymentRequest.cartIdList, buyerId)

        val couponToBuyer =
            couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(paymentRequest.couponIdList, buyerId)

        couponToBuyer.forEach {
            if (it.coupon.expiredAt < LocalDateTime.now()) throw RuntimeException("쿠폰 유효 시간이 만료 되었습니다")
        }

        val productPrice = orderMasterRepository.discountTotalPriceList(buyerId, couponToBuyer)

        val orderMaster = orderMasterRepository.saveAndFlush(OrderMaster())

        orderDetailsRepository.saveAll(
            cart.map {
                OrderDetails(
                    orderStatus = OrderStatus.ORDERED,
                    complainStatus = ComplainStatus.NONE,
                    buyer = buyer,
                    product = it.product,
                    orderMasterId = orderMaster.id!!,
                    productQuantity = it.quantity,
                    shopId = it.shopId,
                    totalPrice = productPrice[it.id]!!,
                )
            }
        )

        couponToBuyer.map { it.useCoupon() }

        cart.forEach {
            if (it.product.productBackOffice!!.quantity < it.quantity) throw RuntimeException("재고가 부족 합니다")
            it.product.productBackOffice!!.quantity -= it.quantity
        }

        itemCartRepository.deleteAll(cart)

        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${orderMaster.id}")
    }
}