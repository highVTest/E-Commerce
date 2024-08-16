package com.highv.ecommerce.common.image.controller

import com.highv.ecommerce.common.image.dto.ImageUrlResponse
import com.highv.ecommerce.common.image.service.ImageService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1")
class ImageController(
    private val imageService: ImageService
) {

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @PostMapping("/image")
    fun uploadImage(
        @RequestPart(value = "file", required = false) file: MultipartFile,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<ImageUrlResponse> = ResponseEntity.ok(imageService.uploadImage(file))

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('BUYER')")
    @PostMapping("/images")
    fun uploadImages(
        @RequestPart files: List<MultipartFile>,
        @AuthenticationPrincipal user: UserPrincipal,
    ): ResponseEntity<List<ImageUrlResponse>> = ResponseEntity.ok(imageService.uploadImages(files))


}

