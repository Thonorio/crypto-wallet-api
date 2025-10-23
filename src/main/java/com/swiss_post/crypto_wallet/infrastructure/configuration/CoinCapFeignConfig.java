package com.swiss_post.crypto_wallet.infrastructure.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoinCapFeignConfig {

    private final CoinCapProperties properties;

    public CoinCapFeignConfig(CoinCapProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("Authorization", "Bearer " + properties.getKey());
    }
}