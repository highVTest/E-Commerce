package com.highv.ecommerce.domain.order_master.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.order_master.entity.OrderMaster

interface OrderMasterRepository {

    fun saveAndFlush(productsOrder: OrderMaster): OrderMaster

    fun findByIdOrNull(id: Long): OrderMaster?

    fun save(productsOrder: OrderMaster): OrderMaster

    fun findByIdIn(ids: List<Long>): List<OrderMaster>

    fun findByIdInOrderByIdDesc(ids: Set<Long>): List<OrderMaster>

    fun discountTotalPriceList(buyerId: Long, couponIdList: List<CouponToBuyer>): Map<Long, Int>
}