package com.highv.ecommerce.Oauth.client.config

import com.highv.ecommerce.Oauth.converter.OAuthProviderConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(OAuthProviderConverter())
    }
}