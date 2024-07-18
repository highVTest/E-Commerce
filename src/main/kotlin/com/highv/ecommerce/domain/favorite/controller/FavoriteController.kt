package com.highv.ecommerce.domain.favorite.controller

import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    ): ResponseEntity<String> = ResponseEntity
        .status(HttpStatus.OK)
        .body(favoriteService.management(productId, user.id))
}