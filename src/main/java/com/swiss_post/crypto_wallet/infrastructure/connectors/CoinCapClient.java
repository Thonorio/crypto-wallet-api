package com.swiss_post.crypto_wallet.infrastructure.connectors;

import com.swiss_post.crypto_wallet.infrastructure.configuration.CoinCapFeignConfig;
import com.swiss_post.crypto_wallet.infrastructure.connectors.dto.CoinCapResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "coinCapClient",
        url = "${coincap.base-url}",
        configuration = CoinCapFeignConfig.class
)
public interface CoinCapClient {

    @GetMapping("/assets")
    CoinCapResponse getAsset();

    @GetMapping("/price/bysymbol/{symbol}")
    CoinCapResponse getPrice(@PathVariable("symbol") String symbol);
}