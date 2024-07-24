package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.dto.CouponRequest
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.slf4j.LoggerFactory
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
    private val couponToBuyerRepository: CouponToBuyerRepository
    ){

    @Transactional
    fun requestPayment(buyerId: Long, couponRequest: CouponRequest, cartId: Long): DefaultResponse {

        val cart = itemCartRepository.findAllByBuyerId(buyerId)

        val couponToBuyer = couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(couponRequest.couponIdList,buyerId)

        val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw RuntimeException("구매자 정보가 존재 하지 않습니다")

        val productPrice = orderMasterRepository.discountTotalPriceList(buyerId, couponToBuyer)

        val orderMaster = orderMasterRepository.saveAndFlush(
            OrderMaster(
                regDateTime = LocalDateTime.now(),
            )
        )

        orderDetailsRepository.saveAll(
            cart.mapIndexed { index, it ->
                OrderDetails(
                    orderStatus = OrderStatus.ORDERED,
                    complainStatus = ComplainStatus.NONE,
                    buyer = buyer,
                    product = it.product,
                    orderMaster = orderMaster,
                    productQuantity = it.quantity,
                    shopId = it.product.shop.id!!,
                    totalPrice = productPrice[it.id]!!,
                )
            }

        )

        couponToBuyer.map { it.useCoupon() }

        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${orderMaster.id}")
    }

}