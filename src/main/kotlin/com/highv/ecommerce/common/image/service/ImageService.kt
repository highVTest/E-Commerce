package com.highv.ecommerce.common.image.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.image.dto.ImageRequest
import com.highv.ecommerce.common.image.dto.ImageUrlResponse
import com.highv.ecommerce.common.image.repository.ImageRepository
import com.highv.ecommerce.infra.s3.S3Manager
import com.highv.ecommerce.infra.s3.entity.Image
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(
    private val s3Manager: S3Manager,
    private val imageRepository: ImageRepository

) {

    fun uploadImage(file: MultipartFile, request: ImageRequest): ImageUrlResponse {
        s3Manager.uploadFile(listOf(file))
        val imageUrl = s3Manager.getFile(file.originalFilename)
        val image = Image(

            usagePath = request.usagePath,
            imageUrl = listOf(imageUrl)


        )
        imageRepository.save(image)

        return ImageUrlResponse(imageUrl = imageUrl)
    }

    fun uploadImages(files: List<MultipartFile>, request: ImageRequest): List<ImageUrlResponse> {

        if (files.size > 9) {
            throw CustomRuntimeException(409, "이미지는 최대 9장만 등록 가능합니다.")
        } else if (files.isEmpty()) {
            throw CustomRuntimeException(409, "이미지를 등록해주세요")
        }

        val imageUrls: MutableList<ImageUrlResponse> = mutableListOf()

        files.forEach {
            val imageUrl = s3Manager.getFile(it.originalFilename)
            val image = Image(

                usagePath = request.usagePath,
                imageUrl = listOf(imageUrl)

            )
            imageRepository.save(image)

            s3Manager.uploadFile(files)

        }

        return imageUrls
    }

    fun getImage(id: String): List<ImageUrlResponse> {
        val image = imageRepository.findByIdOrNull(id)

        return  mutableListOf(image)

    }
}