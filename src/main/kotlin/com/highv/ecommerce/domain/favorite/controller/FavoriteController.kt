package com.highv.ecommerce.domain.favorite.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.favorite.dto.FavoriteResponse
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/favorite")
class FavoriteController(
    private val favoriteService: FavoriteService
) {

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/{productId}")
    fun favoriteManagement(
        @PathVariable("productId") productId: Long,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<DefaultResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(favoriteService.management(productId, user.id))

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping()
    fun getFavorites(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<List<FavoriteResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(favoriteService.getFavorites(user.id))
}