package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.BuyerNotFoundException
import com.highv.ecommerce.common.exception.CartEmptyException
import com.highv.ecommerce.common.exception.CouponExpiredException
import com.highv.ecommerce.common.exception.InsufficientStockException
import com.highv.ecommerce.common.innercall.TxAdvice
import com.highv.ecommerce.common.lock.service.RedisLockService
import com.highv.ecommerce.domain.backoffice.repository.ProductBackOfficeRepository
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.dto.PaymentRequest
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderMasterService(
    private val orderMasterRepository: OrderMasterRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val itemCartRepository: ItemCartRepository,
    private val buyerRepository: BuyerRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val redisLockService: RedisLockService,
    private val txAdvice: TxAdvice,
    private val productBackOfficeRepository: ProductBackOfficeRepository
) {

    fun requestPayment(buyerId: Long, paymentRequest: PaymentRequest): DefaultResponse {

        val key = "락락"
        var masterId = 0L
        kotlin.runCatching {
            redisLockService.runExclusiveWithRedissonLock(key, 50) {
                val buyer =
                    buyerRepository.findByIdOrNull(buyerId) ?: throw BuyerNotFoundException(404, "구매자 정보가 존재하지 않습니다")
                if (paymentRequest.cartIdList.isEmpty()) throw CartEmptyException(400, "장바구니 에서 아이템 목록을 선택해 주세요")

                val cart = itemCartRepository.findAllByIdAndBuyerId(paymentRequest.cartIdList, buyerId)
                val couponToBuyer =
                    couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(
                        paymentRequest.couponIdList,
                        buyerId
                    )


                couponToBuyer.forEach {
                    if (it.coupon.expiredAt < LocalDateTime.now()) throw CouponExpiredException(
                        400,
                        "쿠폰 유효 시간이 만료 되었습니다"
                    )
                }

                val productPrice = orderMasterRepository.discountTotalPriceList(buyerId, couponToBuyer)

                couponToBuyer.forEach { it.useCoupon() }

                //트랜잭션 전파 수준 변경

                val orderMaster = txAdvice.run { orderSave(buyer, cart, productPrice) }

                masterId = orderMaster.id!!
                itemCartRepository.deleteAll(cart)
            }
        }.getOrThrow()
        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : $masterId")
    }

    fun orderSave(buyer: Buyer, cart: List<ItemCart>, productPrice: Map<Long, Int>): OrderMaster {
        cart.forEach {
            if (it.product.productBackOffice!!.quantity < it.quantity)
                throw InsufficientStockException(400, "재고가 부족 합니다")
            it.product.productBackOffice!!.quantity -= it.quantity
            it.product.productBackOffice!!.soldQuantity += it.quantity
            productBackOfficeRepository.saveAndFlush(it.product.productBackOffice!!)
        }
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
                    shop = it.shop,
                    totalPrice = productPrice[it.id]!!,
                )
            }
        )
        return orderMaster
    }

}

