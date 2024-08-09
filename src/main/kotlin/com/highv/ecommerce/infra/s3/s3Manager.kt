package com.highv.ecommerce.infra.s3

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.highv.ecommerce.common.exception.CustomRuntimeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Component
class S3Manager(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}") val bucket: String
) {

    fun uploadFile(files: List<MultipartFile>): List<String> {
        val util = FileUtil()
        val types: MutableList<String?> = mutableListOf()

        files.forEach {
            types.add(util.validImgFile(it.inputStream))
        }

        types.forEach { // 만약 하나라도 문제가 있으면 안됨
            if (it.isNullOrEmpty()) {
                throw CustomRuntimeException(400, "이미지 파일만 업로드 해주세요")
            }
        }

        val uuids: List<UUID> = (1..files.size).map {
            UUID.randomUUID()
        }

        val filesName: MutableList<String> = mutableListOf<String>()

        val metadata: MutableList<ObjectMetadata> = mutableListOf()

        for (i in uuids.indices) {
            filesName.add("${uuids[i]}-${files[i].originalFilename}")
            metadata.add(ObjectMetadata().apply { // 파일의 컨텐츠 타입과 크기를 설정
                contentType = types[i]
                contentLength = files[i].size
            })
        }

        for (i in filesName.indices) {
            amazonS3Client.putObject(
                bucket,
                filesName[i],
                files[i].inputStream,
                metadata[i]
            ) // Amazon S3 클라이언트를 사용하여 파일을 S3 버킷에 업로드
        }


        return filesName.map {
            getFile(it)
        }
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
