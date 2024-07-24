package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_master.dto.CouponRequest
import com.highv.ecommerce.domain.order_master.dto.OrderStatusRequest
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
    private val productRepository: ProductRepository
    ){

    @Transactional
    fun requestPayment(buyerId: Long, couponRequest: CouponRequest): DefaultResponse {

        //TODO(카트 아이디 를 조회 해서 물건을 가져 온다 -> List<CartItem>)

        val itemCart = itemCartRepository.findAllByBuyerId(buyerId)

        val totalPrice = orderMasterRepository.discountTotalPriceList(buyerId, couponRequest.couponIdList)

        val product = productRepository.findByIdOrNull(1L)



        val buyer = buyerRepository.findByIdOrNull(buyerId)!!

        //TODO(만약에 CartItem 에 ProductId 가 Coupon 의 ProductId와 일치할 경우 CartItem 의 가격을 임시로 업데이트)

        val orderMaster = orderMasterRepository.saveAndFlush(
            OrderMaster(
                regDateTime = LocalDateTime.now(),
            )
        )


        orderDetailsRepository.saveAll(
            itemCart.map {
                OrderDetails(
                    orderStatus = OrderStatus.ORDERED,
                    complainStatus = ComplainStatus.NONE,
                    buyer = buyer,
                    product = product!!,
                    orderMaster = orderMaster,
                    productQuantity = 1
                )
            }

        )


        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${orderMaster.id}")
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, orderStatusRequest: OrderStatusRequest, sellerId: Long): DefaultResponse {

//        val order = productsOrderRepository.findByIdOrNull(orderId) ?: throw RuntimeException()
//
//        order.update(orderStatusRequest)
//
//        productsOrderRepository.save(order)

        return DefaultResponse.from("주문 상태 변경이 완료 되었습니다. 변경된 상태 : ${orderStatusRequest.statusCode.name}")
    }



}