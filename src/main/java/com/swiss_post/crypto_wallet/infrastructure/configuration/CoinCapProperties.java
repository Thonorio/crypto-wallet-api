package com.swiss_post.crypto_wallet.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "coincap")
public class CoinCapProperties {

    private String key;
    private String baseUrl;
    private long interval = 60000;
    private List<String> symbols;
}