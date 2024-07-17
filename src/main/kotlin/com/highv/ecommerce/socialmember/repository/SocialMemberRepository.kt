package com.highv.ecommerce.socialmember.repository

import com.highv.ecommerce.socialmember.entity.KakaoSocialMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialMemberRepository : JpaRepository<KakaoSocialMember, Long> {
    fun findByProviderNameAndProviderId(providerName: String, providerId: String): KakaoSocialMember?
}