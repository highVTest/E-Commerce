package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.aop.StopWatch
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
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
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
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val redisLockService: RedisLockService,
    private val txAdvice: TxAdvice,
    private val productBackOfficeRepository: ProductBackOfficeRepository,
) {
    
    fun requestPayment(buyerId: Long, paymentRequest: PaymentRequest): DefaultResponse {

        val key = "락락"
        var masterId = 0L
        kotlin.runCatching {
            redisLockService.runExclusiveWithRedissonLock(key, 50) {

                val cart = itemCartRepository.findAllByIdAndBuyerId(paymentRequest.cartIdList, buyerId)

                val couponToBuyerList =
                    couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedFalse(
                        paymentRequest.couponIdList,
                        buyerId
                    )

                val totalPrice = mutableMapOf<Long, Int>()

                cart.map { cartItem ->

                    if(couponToBuyerList.isEmpty()){
                        val price = (cartItem.product.productBackOffice!!.price * cartItem.quantity)
                        totalPrice[cartItem.id!!] = price
                    }else{
                        couponToBuyerList.forEach {
                            if (it.coupon.expiredAt < LocalDateTime.now()) throw CouponExpiredException(
                                400,
                                "쿠폰 유효 시간이 만료 되었습니다"
                            )
                            if (cartItem.product.id == it.coupon.product.id) {
                                when (it.coupon.discountPolicy) {
                                    DiscountPolicy.DISCOUNT_RATE -> {
                                        val price = (cartItem.quantity * cartItem.product.productBackOffice!!.price) - (
                                                (cartItem.quantity * cartItem.product.productBackOffice!!.price) * ((it.coupon.discount).toDouble() / 100.0)).toInt()
                                        totalPrice[cartItem.id!!] = price
                                        it.useCoupon()
                                    }

                                    DiscountPolicy.DISCOUNT_PRICE -> {
                                        val price = (cartItem.product.productBackOffice!!.price * cartItem.quantity) - it.coupon.discount
                                        totalPrice[cartItem.id!!] = price
                                        it.useCoupon()
                                    }
                                }

                            } else {
                                val price = (cartItem.product.productBackOffice!!.price * cartItem.quantity)
                                totalPrice[cartItem.id!!] = price
                            }
                        }
                    }
                }

                //트랜잭션 전파 수준 변경

                val orderMaster = txAdvice.run { orderSave(cart[0].buyer, cart, totalPrice) }

                masterId = orderMaster.id!!
                itemCartRepository.deleteAll(cart)
            }
        }.getOrThrow()
        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : $masterId")
    }

    fun orderSave(buyer: Buyer, cart: List<ItemCart>, productPrice: Map<Long, Int>): OrderMaster {


        cart.forEach {
            val productBackOffice = it.product.productBackOffice!!
            if (productBackOffice.quantity < it.quantity)
                throw InsufficientStockException(400, "재고가 부족 합니다")
            productBackOffice.quantity -= it.quantity
            productBackOffice.soldQuantity += it.quantity
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

