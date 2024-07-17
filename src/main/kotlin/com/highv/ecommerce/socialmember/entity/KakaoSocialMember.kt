package com.highv.ecommerce.socialmember.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

//Kakao 소셜 로그인 사용자 정보를 저장
@Entity
class KakaoSocialMember(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_member_id")
    var id: Long? = null,

    val providerName: String, // KAKAO,NAVER
    val providerId: String, //카카오에서 제공하는 Id
    var nickname: String,

) {
}