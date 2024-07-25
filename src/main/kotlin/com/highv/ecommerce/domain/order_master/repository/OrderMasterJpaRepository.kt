package com.highv.ecommerce.domain.order_master.repository

import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import org.springframework.data.jpa.repository.JpaRepository

interface OrderMasterJpaRepository : JpaRepository<OrderMaster, Long> {
    fun findByIdIn(ids: List<Long>): List<OrderMaster>
    fun findByIdInOrderByIdDesc(ids: List<Long>): List<OrderMaster>
}