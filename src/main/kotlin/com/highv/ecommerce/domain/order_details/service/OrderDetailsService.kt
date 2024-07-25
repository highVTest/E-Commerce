package com.highv.ecommerce.domain.order_details.service

import com.highv.ecommerce.domain.buyer.dto.response.BuyerHistoryProductResponse
import com.highv.ecommerce.domain.buyer.dto.response.BuyerOrderResponse
import com.highv.ecommerce.domain.buyer.dto.response.BuyerOrderShopResponse
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.ComplainType
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderDetailsService(
    private val orderDetailsRepository: OrderDetailsRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val couponRepository: CouponRepository,
    private val orderMasterRepository: OrderMasterRepository
) {

    @Transactional
    fun buyerRequestComplain(
        buyerOrderStatusRequest: BuyerOrderStatusRequest,
        buyerId: Long,
        shopId: Long,
        orderId: Long
    ): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(shopId, orderId, buyerId)

        orderDetails.map {
            it.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
        }

        orderDetailsRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(buyerOrderStatusRequest.complainType, "요청 완료 되었습니다")
    }

    fun getBuyerOrderDetails(buyerId: Long): List<BuyerOrderResponse> {
        /*  // 주문 내역 전체 불러오기
        * 1. oderDetails 에서 buyerId로 목록 전체 가져오기
        * 2. 가져온 주문 내역에서 주문 별로 나누고 가게별로 분리
        * 3. 주문 내역 목록 반환하기
        * */

        val orderDetails: List<OrderDetails> = orderDetailsRepository.findAllByBuyerId(buyerId)
        val orderMasters: List<OrderMaster> = orderMasterRepository.findByIdIn(orderDetails.map { it.orderMasterId })

        val orderMasterGroup: MutableMap<Long, MutableMap<Long, BuyerOrderShopResponse>> = mutableMapOf()

        orderDetails.forEach {
            if (!orderMasterGroup.containsKey(it.orderMasterId)) {
                orderMasterGroup[it.orderMasterId] = mutableMapOf()
            }
            if (!orderMasterGroup[it.orderMasterId]!!.contains(it.shopId)) {
                orderMasterGroup[it.orderMasterId]!![it.shopId] = BuyerOrderShopResponse(it.shopId, mutableListOf())
            }
            orderMasterGroup[it.orderMasterId]!![it.shopId]!!.productsOrders.add(BuyerHistoryProductResponse.from(it))
        }

        val orderMasterAndShopGroup: MutableMap<Long, MutableList<BuyerOrderShopResponse>> = mutableMapOf()

        orderMasterGroup.forEach {
            if (!orderMasterAndShopGroup.containsKey(it.key)) {
                orderMasterAndShopGroup[it.key] = mutableListOf()
            }
            it.value.forEach { item ->
                orderMasterAndShopGroup[it.key]?.add(item.value)
            }

        }


        return orderMasters.map {
            BuyerOrderResponse(
                orderMasterId = it.id!!,
                orderRegisterDate = it.regDateTime,
                orderMasterAndShopGroup[it.id]!!
            )
        }
    }

    @Transactional
    fun requestComplainReject(
        sellerOrderStatusRequest: SellerOrderStatusRequest,
        shopId: Long,
        orderId: Long
    ): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(
            shopId,
            orderId,
            sellerOrderStatusRequest.buyerId
        )
        val complainType =
            if (orderDetails[0].complainStatus == ComplainStatus.REFUND_REQUESTED) ComplainType.REFUND
            else ComplainType.EXCHANGE

        orderDetails.map {
            it.sellerUpdate(OrderStatus.ORDERED, sellerOrderStatusRequest, orderDetails[0].complainStatus)
        }

        orderDetailsRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(complainType, "전체 요청 거절 완료 되었습니다")
    }

    fun getSellerOrderDetailsAll(shopId: Long, sellerId: Long): List<ProductsOrderResponse> {

        val orderDetails = orderDetailsRepository.findAllByShopId(shopId)

        return orderDetails.map { ProductsOrderResponse.from(it) }
    }

    fun getSellerOrderDetailsBuyer(shopId: Long, orderId: Long, buyerId: Long): List<ProductsOrderResponse> {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(shopId, orderId, buyerId)

        return orderDetails.map { ProductsOrderResponse.from(it) }
    }

    @Transactional
    fun requestComplainAccept(
        shopId: Long,
        orderId: Long,
        sellerOrderStatusRequest: SellerOrderStatusRequest
    ): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(
            shopId,
            orderId,
            sellerOrderStatusRequest.buyerId
        )
        val coupons = couponRepository.findAllByProductId(orderDetails.map { it.product.id!! })
        val complainType =
            if (orderDetails[0].complainStatus == ComplainStatus.REFUND_REQUESTED) ComplainType.REFUND
            else ComplainType.EXCHANGE
        when (orderDetails[0].complainStatus) {
            ComplainStatus.REFUND_REQUESTED -> {
                orderDetails.map {
                    it.sellerUpdate(OrderStatus.ORDER_CANCELED, sellerOrderStatusRequest, ComplainStatus.REFUNDED)
                    it.product.productBackOffice!!.quantity += it.productQuantity
                    val couponToBuyerList = couponToBuyerRepository.findAllByCouponIdAndBuyerIdAndIsUsedTrue(
                        coupons,
                        sellerOrderStatusRequest.buyerId
                    )

                    couponToBuyerList.map { couponToBuyer -> couponToBuyer.returnCoupon() }
                }
            }

            ComplainStatus.EXCHANGE_REQUESTED -> {
                orderDetails.map {
                    it.sellerUpdate(OrderStatus.PRODUCT_PREPARING, sellerOrderStatusRequest, ComplainStatus.EXCHANGED)

                }
            }

            else -> throw RuntimeException("구매자가 환불 및 교환 요청을 하지 않았 거나 요청 처리가 완료 되었습니다")
        }


        orderDetailsRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(complainType, "전체 요청 승인 완료 되었습니다")
    }
}