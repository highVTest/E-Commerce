package com.highv.ecommerce.infra.s3

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.highv.ecommerce.common.exception.CustomRuntimeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class S3Manager(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}") val bucket: String
) {

    fun uploadFile(file: MultipartFile) {
        val util = FileUtil()
        val type = util.validImgFile(file.inputStream)
        if (type.isNullOrEmpty()) {
            throw CustomRuntimeException(400, "이미지 파일만 업로드 해주세요")
        }
        val fileName = file.originalFilename

        val metadata = ObjectMetadata().apply { // 파일의 컨텐츠 타입과 크기를 설정
            contentType = type
            contentLength = file.size
        }

        amazonS3Client.putObject(bucket, fileName, file.inputStream, metadata) // Amazon S3 클라이언트를 사용하여 파일을 S3 버킷에 업로드
    }

    fun getFile(fileName: String?): String {
        if (fileName.isNullOrBlank()) {
            throw CustomRuntimeException(400, "이미지 명이 없음")
        }
        return amazonS3Client.getUrl(bucket, fileName).toString()
    }

    fun deleteFile(fileName: String) {
        amazonS3Client.deleteObject(bucket, fileName)
    }
}
