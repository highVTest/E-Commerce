package com.highv.ecommerce.infra.email

import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MailService(
    private val emailSender: JavaMailSender
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
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
            throw RuntimeException("이메일 보내기 실패")
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