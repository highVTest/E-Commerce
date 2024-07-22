package com.highv.ecommerce.domain.buyer_history.repository

import com.highv.ecommerce.domain.buyer_history.entity.BuyerHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BuyerHistoryRepository: JpaRepository<BuyerHistory, Long> {
}