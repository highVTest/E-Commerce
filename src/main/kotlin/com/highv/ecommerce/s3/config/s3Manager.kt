package com.highv.ecommerce.s3.config

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Component
class S3Manager(
    private val amazonS3Client: AmazonS3Client,
    @Value ("\${cloud.aws.s3.bucket}") val bucket: String
) {

    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile):String {
        val fileName = file.originalFilename
        val fileUrl = "https://$bucket/test/${fileName}"

        val metadata = ObjectMetadata().apply { // 파일의 컨텐츠 타입과 크기를 설정
            contentType = file.contentType
            contentLength = file.size
        }

            amazonS3Client.putObject(bucket, fileName, file.inputStream, metadata) // Amazon S3 클라이언트를 사용하여 파일을 S3 버킷에 업로드

        return ResponseEntity.ok(fileUrl).toString()
    }
}
