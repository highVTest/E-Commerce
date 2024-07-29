package com.highv.ecommerce.infra.s3

import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream


@Component
class FileUtil {

    private val tika = Tika()

    fun validImgFile(inputStream: InputStream): String? {
        return try {
            val notValidTypeList = listOf("image/jpeg", "application/pdf", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp")

            val mimeType = tika.detect(inputStream)
            println("MimeType : $mimeType")

//            mimeType = notValidTypeList.any { it.equals(mimeType, ignoreCase = true) }
            if (notValidTypeList.contains(mimeType)) {
                mimeType
            } else {
                ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}