package com.highv.ecommerce.infra.email

import com.highv.ecommerce.common.exception.CustomRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailUtils(
    private val emailSender: JavaMailSender
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendEmail(
        toEmail: String,
        title: String,
        text: String
    ) {
        val emailForm: SimpleMailMessage = createEmailForm(toEmail, title, text)
        try {
            emailSender.send(emailForm)
        } catch (ex: RuntimeException) {
            logger.debug("MailService.sendEmail exception occur toEmail: ${toEmail}, title : ${title}, text : ${text}")
            throw CustomRuntimeException(500, "이메일 보내기 실패")
        }
    }

    private fun createEmailForm(
        toEmail: String,
        title: String,
        text: String
    ): SimpleMailMessage {
        val message: SimpleMailMessage = SimpleMailMessage()
        message.setTo(toEmail)
        message.subject = title
        message.text = "인증번호 : ${text}"
        return message
    }
}