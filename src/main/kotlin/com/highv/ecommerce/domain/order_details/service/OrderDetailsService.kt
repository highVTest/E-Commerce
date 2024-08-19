package com.highv.ecommerce.domain.order_details.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.exception.InvalidRequestException
import com.highv.ecommerce.common.exception.ModelNotFoundException
import com.highv.ecommerce.common.innercall.TxAdvice
import com.highv.ecommerce.common.lock.service.RedisLockService
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderDetailProductResponse
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderResponse
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderShopResponse
import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.UpdateDeliveryStatusRequest
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.ComplainType
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_master.repository.OrderMasterRepository
import com.highv.ecommerce.domain.seller.shop.entity.Shop
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderDetailsService(
    private val orderDetailsRepository: OrderDetailsRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val couponRepository: CouponRepository,
    private val orderMasterRepository: OrderMasterRepository,
    private val lockService: RedisLockService,
    private val txAdvice: TxAdvice
) {

    @Transactional
    fun buyerRequestComplain(
        buyerOrderStatusRequest: BuyerOrderStatusRequest,
        buyerId: Long,
        shopId: Long,
        orderId: Long
    ): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterId(shopId, orderId)

        if (orderDetails.isEmpty()) throw ModelNotFoundException(409, "주문한 상품이 존재 하지 않습니다")

        if (orderDetails[0].orderStatus == OrderStatus.PENDING) throw CustomRuntimeException(
            400,
            "이미 환불이나 교환이 요청된 상품 입니다"
        )

        orderDetails.forEach {
            it.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
        }

        return OrderStatusResponse.from(buyerOrderStatusRequest.complainType, "요청 완료 되었습니다")
    }

    fun getBuyerOrders(buyerId: Long): List<BuyerOrderResponse> {

        val orderDetails: List<OrderDetails> = orderDetailsRepository.findAllByBuyerId(buyerId)
        val orderMasters: List<OrderMaster> =
            orderMasterRepository.findByIdInOrderByIdDesc(orderDetails.map { it.orderMasterId }.toSet<Long>())

        val orderMasterShopGroup: MutableMap<String, Pair<Shop, MutableList<BuyerOrderDetailProductResponse>>> =
            mutableMapOf()

        orderDetails.forEach {
            val key = "${it.orderMasterId}+${it.shop.id}"
            if (!orderMasterShopGroup.containsKey(key)) {
                orderMasterShopGroup[key] = Pair(it.shop, mutableListOf())
            }
            orderMasterShopGroup[key]!!.second.add(BuyerOrderDetailProductResponse.from(it))
        }

        val orderMasterGroup: MutableMap<Long, MutableList<BuyerOrderShopResponse>> = mutableMapOf()

        orderMasterShopGroup.forEach {
            val key = it.key.split("+")
            val keyOrderId = key[0].toLong()

            if (!orderMasterGroup.containsKey(keyOrderId)) {
                orderMasterGroup[keyOrderId] = mutableListOf()
            }

            orderMasterGroup[keyOrderId]!!.add(BuyerOrderShopResponse.from(it.value.first, it.value.second))

        }

        return orderMasters.map {
            BuyerOrderResponse(
                orderMasterId = it.id!!,
                orderRegisterDate = it.regDateTime,
                orderShopDetails = orderMasterGroup[it.id]!!
            )
        }
    }

    fun getBuyerOrderDetails(buyerId: Long, orderId: Long): BuyerOrderResponse {

        val orderMaster: OrderMaster =
            orderMasterRepository.findByIdOrNull(orderId) ?: throw CustomRuntimeException(404, "주문 내역이 없습니다.")

        val orderDetails: List<OrderDetails> = orderDetailsRepository.findAllByBuyerIdAndOrderMasterId(buyerId, orderId)

        if (orderDetails.isEmpty()) {
            throw CustomRuntimeException(404, "주문 내역이 없습니다.")
        }

        val shopGroup: MutableMap<Shop, MutableList<BuyerOrderDetailProductResponse>> = mutableMapOf()

        orderDetails.forEach {
            if (!shopGroup.containsKey(it.shop)) {
                shopGroup[it.shop] = mutableListOf()
            }
            shopGroup[it.shop]?.add(BuyerOrderDetailProductResponse.from(it))
        }

        val orderShopDetails: MutableList<BuyerOrderShopResponse> = mutableListOf()

        shopGroup.forEach {
            orderShopDetails.add(BuyerOrderShopResponse.from(it.key, it.value))
        }

        return BuyerOrderResponse(
            orderMasterId = orderMaster.id!!,
            orderRegisterDate = orderMaster.regDateTime,
            orderShopDetails = orderShopDetails
        )
    }

    @Transactional
    fun requestComplainReject(
        sellerOrderStatusRequest: SellerOrderStatusRequest,
        shopId: Long,
        orderId: Long,
        sellerId: Long
    ): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterId(
            shopId,
            orderId,
        )

        if (orderDetails.isEmpty()) throw CustomRuntimeException(400, "주문 정보가 존재 하지 않습니다")
        if (orderDetails[0].product.shop.sellerId != sellerId) throw CustomRuntimeException(400, "다른 상점의 정보 입니다")

        val complainType =
            if (orderDetails[0].complainStatus == ComplainStatus.REFUND_REQUESTED) ComplainType.REFUND
            else ComplainType.EXCHANGE

        orderDetails.map {
            it.sellerUpdate(OrderStatus.ORDERED, sellerOrderStatusRequest, orderDetails[0].complainStatus)
        }

        return OrderStatusResponse.from(complainType, "전체 요청 거절 완료 되었습니다")
    }

    fun getSellerOrderDetailsAll(shopId: Long, orderStatus: OrderStatus, sellerId: Long): List<SellerOrderResponse> {

        val orderDetails = orderDetailsRepository.findAllByShopIdOrderStatus(shopId, orderStatus)

        if (orderDetails.isEmpty()) throw CustomRuntimeException(404, "검색 결과가 존재 하지 않습니다")

        val orderMasters =
            orderMasterRepository.findByIdInOrderByIdDesc(orderDetails.map { it.orderMasterId }.toSet())

        val orderMasterGroup: MutableMap<Long, MutableList<OrderDetails>> = mutableMapOf()

        orderDetails.forEach {
            if (!orderMasterGroup.containsKey(it.orderMasterId)) {
                orderMasterGroup[it.orderMasterId] = mutableListOf()
            }

            orderMasterGroup[it.orderMasterId]?.add(it)

        }


        return orderMasters.map { SellerOrderResponse.from(it, orderMasterGroup[it.id!!]!!) }
    }

    fun getSellerOrderDetailsBuyer(shopId: Long, orderId: Long): SellerOrderResponse {

        val orderMaster =
            orderMasterRepository.findByIdOrNull(orderId) ?: throw CustomRuntimeException(409, "주문 내역이 없습니다.")

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterId(shopId, orderId)



        return SellerOrderResponse.from(orderMaster, orderDetails)
    }

    fun requestComplainAccept(
        shopId: Long,
        orderId: Long,
        sellerOrderStatusRequest: SellerOrderStatusRequest,
        sellerId: Long
    ): OrderStatusResponse {

        val lockKey = "${shopId}_${sellerId}"

        var complainType = ComplainType.REFUND

        kotlin.runCatching {
            lockService.runExclusiveWithRedissonLock(lockKey, 1) {
                val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterId(
                    shopId,
                    orderId
                )

                val couponIdList = couponRepository.findAllByProductId(orderDetails.map { it.product.id!! })

                complainType =
                    if (orderDetails[0].complainStatus == ComplainStatus.REFUND_REQUESTED) ComplainType.REFUND
                    else ComplainType.EXCHANGE

                txAdvice.run { setAcceptLogic(orderDetails, sellerOrderStatusRequest, couponIdList) }

                orderDetailsRepository.saveAll(orderDetails)
            }
        }.getOrThrow()

        return OrderStatusResponse.from(complainType, "전체 요청 승인 완료 되었습니다")
    }

    private fun setAcceptLogic(
        orderDetails: List<OrderDetails>,
        sellerOrderStatusRequest: SellerOrderStatusRequest,
        couponIdList: List<Long>
    ) {

        when (orderDetails[0].complainStatus) {

            ComplainStatus.REFUND_REQUESTED -> {

                orderDetails.map {
                    val productBackOffice = it.product.productBackOffice!!
                    it.sellerUpdate(it.orderStatus, sellerOrderStatusRequest, ComplainStatus.REFUNDED)

                    productBackOffice.quantity += it.productQuantity
                    productBackOffice.soldQuantity -= it.productQuantity

                    couponToBuyerRepository.saveAllByCouponIdAndBuyerIdAndIsUsedTrue(
                        couponIdList,
                        it.buyer.id!!
                    )

                }
            }

            ComplainStatus.EXCHANGE_REQUESTED -> {
                orderDetails.map {
                    it.sellerUpdate(it.orderStatus, sellerOrderStatusRequest, ComplainStatus.EXCHANGED)

                }
            }

            else -> throw InvalidRequestException(400, "구매자가 환불 및 교환 요청을 하지 않았 거나 요청 처리가 완료 되었습니다")
        }
    }

    @Transactional
    fun updateProductsDelivery(
        orderMasterId: Long,
        shopId: Long,
        request: UpdateDeliveryStatusRequest
    ): DefaultResponse {
        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterId(shopId, orderMasterId)

        if (orderDetails.isEmpty()) {
            throw InvalidRequestException(400, "변경 가능한 상품들이 없습니다.")
        }

        orderDetails.forEach {
            it.updateDeliveryStatus(request.deliveryStatus)
        }

        orderDetailsRepository.saveAll(orderDetails)

        return DefaultResponse("상태 변경이 완료됐습니다. 변경된 상태 : ${request.deliveryStatus}")
    }

    @Transactional
    fun updateDelivery(): DefaultResponse {

        orderDetailsRepository.updateDeliveryStatus(
            changeStatus = OrderStatus.DELIVERED,
            whereStatus = OrderStatus.SHIPPING
        )

        orderDetailsRepository.updateDeliveryStatus(
            changeStatus = OrderStatus.SHIPPING,
            whereStatus = OrderStatus.DELIVERY_PREPARING
        )

        orderDetailsRepository.updateDeliveryStatus(
            changeStatus = OrderStatus.DELIVERY_PREPARING,
            whereStatus = OrderStatus.PRODUCT_PREPARING
        )

        return DefaultResponse("상태를 성공적으로 변경 했습니다.")
    }
}


