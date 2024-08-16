package com.highv.ecommerce.Image
import com.highv.ecommerce.common.image.service.ImageService

import com.highv.ecommerce.infra.s3.S3Manager
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.mock.web.MockMultipartFile

import kotlin.test.Test

class ImageServiceTest() {

    private val s3Manager: S3Manager = mockk()
    private val imageService: ImageService = ImageService(s3Manager)


    @Test
    fun `이미지 파일이 주어졌을 때, 이미지를 업로드`() {
        //GIVEN
        val file = MockMultipartFile("image", "test.jpg", "image/jpeg", "test-image".toByteArray())
        val mockUrl = "https://mockurl.com/test.jpg"


        every { s3Manager.uploadFile(any()) } returns listOf(mockUrl)


        // WHEN: uploadImage 메서드를 호출하면
        val result = imageService.uploadImage(file)

        //THEN
        result.imageUrl shouldBe mockUrl
    }
}
