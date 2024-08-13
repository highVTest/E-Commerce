package com.highv.ecommerce.common.image.controller

import com.highv.ecommerce.common.image.dto.ImageRequest
import com.highv.ecommerce.common.image.dto.ImageUrlResponse
import com.highv.ecommerce.common.image.service.ImageService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/v1")
class ImageController(
    private val imageService: ImageService
) {

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @PostMapping("/image")
    fun uploadImage(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestPart request: ImageRequest,
        @RequestPart(value = "file",required = false) file: MultipartFile?,

    ): ResponseEntity<ImageUrlResponse> = ResponseEntity.ok(imageService.uploadImage(file, user.id, request))

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @PostMapping("/images")
    fun uploadImages(
        @RequestPart files: List<MultipartFile>,
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestPart request: ImageRequest,
    ): ResponseEntity<List<ImageUrlResponse>> = ResponseEntity.ok(imageService.uploadImages(files, user.id, request))

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @GetMapping("/images/{id}")
    fun getImage(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<List<ImageUrlResponse>> = ResponseEntity.ok(imageService.getImage(user.id))
}

