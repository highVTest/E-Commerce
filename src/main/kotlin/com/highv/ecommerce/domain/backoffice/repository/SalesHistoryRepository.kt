package com.highv.ecommerce.domain.backoffice.repository

import com.highv.ecommerce.domain.backoffice.entity.SalesHistory
import org.springframework.data.jpa.repository.JpaRepository

interface SalesHistoryRepository : JpaRepository<SalesHistory, Long> {
    fun findByOrderId(orderId: Long): SalesHistory
}