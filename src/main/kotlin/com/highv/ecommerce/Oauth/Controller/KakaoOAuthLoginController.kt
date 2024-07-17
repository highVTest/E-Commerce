package com.highv.ecommerce.Oauth.Controller

import com.highv.ecommerce.Oauth.Service.KakaoOAuthLoginService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class KakaoOAuthLoginController(
    private val kakaoOAuthLoginService: KakaoOAuthLoginService
) {
    // 로그인 요청이 왔을 때 로그인 페이지로 Redirect
    @GetMapping("/oauth/login/kakao")
    fun redirectLogin(response: HttpServletResponse) {
        val loginUrl = kakaoOAuthLoginService.generateLoginUrl()
        response.sendRedirect(loginUrl)
    }

    // 인증 코드 받아서 로그인 완료처리해주고 AccessToken 반환
    @GetMapping("/oauth/login/callback")
    fun callback(
        @RequestParam code: String
    ): ResponseEntity<String> {
        val accessToken = kakaoOAuthLoginService.login(code)
        return ResponseEntity.ok(accessToken)
    }
}