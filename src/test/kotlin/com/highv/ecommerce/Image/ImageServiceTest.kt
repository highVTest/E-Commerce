package com.highv.ecommerce.Image
import com.highv.ecommerce.common.image.dto.ImageRequest
import com.highv.ecommerce.common.image.dto.ImageUrlResponse
import com.highv.ecommerce.common.image.entity.UsagePath
import com.highv.ecommerce.common.image.repository.ImageRepository
import com.highv.ecommerce.common.image.service.ImageService

import com.highv.ecommerce.infra.s3.S3Manager
import com.highv.ecommerce.infra.s3.entity.Image
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.mock.web.MockMultipartFile
import kotlin.test.Test

class ImageServiceTest() {

    private val s3Manager: S3Manager = mockk()
    private val imageRepository: ImageRepository = mockk()
    private val imageService: ImageService = ImageService(s3Manager, imageRepository)


    @Test
    fun `이미지 파일이 주어졌을 때, 이미지를 업로드`() {
        //GIVEN
        val file = MockMultipartFile("image", "test.jpg", "image/jpeg", "test-image".toByteArray())
        val request = ImageRequest(usagePath = UsagePath.BUYER, imageName = String()) // UsagePath는 우선 BUYER
        val mockUrl = "https://mockurl.com/test.jpg"
        val imageUrlResponse = ImageUrlResponse(mockUrl)




        every { s3Manager.uploadFile(any()) } returns listOf(mockUrl)
        every { s3Manager.getFile(any()) } returns mockUrl
        every { imageRepository.findByIdOrNull(any()) } returns imageUrlResponse
        every { imageRepository.save(any()) } returns
            Image(imageUrl = listOf(mockUrl), usagePath = request.usagePath).apply { id = "1" }



        // WHEN: uploadImage 메서드를 호출하면
        val result = imageService.uploadImage(file,request)

        //THEN
        result.imageUrl shouldBe mockUrl
    }
}