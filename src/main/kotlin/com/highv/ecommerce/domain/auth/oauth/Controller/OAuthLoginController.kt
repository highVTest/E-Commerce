package com.highv.ecommerce.domain.auth.oauth.Controller

import com.highv.ecommerce.common.dto.AccessTokenResponse
import com.highv.ecommerce.common.type.OAuthProvider
import com.highv.ecommerce.domain.auth.oauth.OAuthClientService
import com.highv.ecommerce.domain.auth.oauth.OAuthLoginService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class OAuthLoginController(
    private val oAuthLoginService: OAuthLoginService,
    private val oAuthClientService: OAuthClientService
) {
    // 로그인 요청이 왔을 때 로그인 페이지로 Redirect
    @GetMapping("/oauth/login/{provider}")
    fun redirectLogin(@PathVariable provider: OAuthProvider,response: HttpServletResponse) {
        val loginUrl = oAuthClientService.generateLoginUrl(provider)
        response.sendRedirect(loginUrl)
    }

    // 인증 코드 받아서 로그인 완료처리해주고 AccessToken 반환
    @GetMapping("/oauth/login/callback/{provider}")
    fun callback(
        @PathVariable provider: OAuthProvider,
        @RequestParam(name = "code") code: String
    ): ResponseEntity<AccessTokenResponse> {
        val accessToken = oAuthLoginService.login(provider,code)
        return ResponseEntity.ok(AccessTokenResponse(accessToken))
    }
}