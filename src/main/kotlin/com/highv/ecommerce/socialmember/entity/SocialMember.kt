package com.highv.ecommerce.socialmember.entity

import com.highv.ecommerce.common.type.OAuthProvider
import jakarta.persistence.*

//Kakao 소셜 로그인 사용자 정보를 저장
@Entity
class SocialMember(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_member_id")
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider, // KAKAO,NAVER
    val providerId: String, //카카오에서 제공하는 Id
    var nickname: String,

) {
}