package com.highv.ecommerce.domain.buyer.repository

import com.highv.ecommerce.domain.buyer.entity.Buyer
import org.springframework.data.jpa.repository.JpaRepository

interface BuyerRepository : JpaRepository<Buyer, Long> {
    fun findByEmail(email: String): Buyer?

    fun findByProviderNameAndProviderId(providerName: String, providerId: String): Buyer?

    fun existsByEmail(email: String): Boolean
}