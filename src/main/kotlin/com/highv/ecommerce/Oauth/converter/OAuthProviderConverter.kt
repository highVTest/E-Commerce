package com.highv.ecommerce.Oauth.converter

import com.highv.ecommerce.common.type.OAuthProvider
import org.springframework.core.convert.converter.Converter

class OAuthProviderConverter : Converter<String, OAuthProvider> {

    override fun convert(source: String): OAuthProvider {
        return runCatching {
            OAuthProvider.valueOf(source.uppercase())
        }.getOrElse {
            throw IllegalArgumentException()
        }
    }
}