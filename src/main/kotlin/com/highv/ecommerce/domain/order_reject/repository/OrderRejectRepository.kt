package com.highv.ecommerce.domain.order_reject.repository

import com.highv.ecommerce.domain.order_reject.entity.OrderReject
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRejectRepository: JpaRepository<OrderReject, Long> {
}