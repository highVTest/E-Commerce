package com.highv.ecommerce.domain.buyer.repository

import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.socialmember.entity.KakaoSocialMember
import org.springframework.data.jpa.repository.JpaRepository

interface BuyerRepository:JpaRepository<Buyer,Long> {
    fun findByEmail(email: String): Buyer?
    fun findByProviderNameAndProviderId(providerName: String, providerId: Long): Buyer?

}