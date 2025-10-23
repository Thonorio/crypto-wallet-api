package com.swiss_post.crypto_wallet.service;

import com.swiss_post.crypto_wallet.infrastructure.configuration.CoinCapProperties;
import com.swiss_post.crypto_wallet.infrastructure.connectors.CoinCapClient;
import com.swiss_post.crypto_wallet.infrastructure.mappers.CoinCapMapper;
import com.swiss_post.crypto_wallet.entity.CoinCapAssetEntity;
import com.swiss_post.crypto_wallet.repository.CoinCapAssetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AssetPriceUpdater {

    private final CoinCapClient coinCapClient;
    private final CoinCapAssetRepository coinCapAssetRepository;
    private final List<String> symbols;

    public AssetPriceUpdater(CoinCapClient coinCapClient,
                             CoinCapAssetRepository coinCapAssetRepository,
                             CoinCapProperties properties) {
        this.coinCapClient = coinCapClient;
        this.coinCapAssetRepository = coinCapAssetRepository;
        this.symbols = properties.getSymbols();
    }

    @Async("priceUpdaterExecutor")
    @Scheduled(fixedDelayString = "#{${coincap.update.interval:60000}}")
    public void updateAssets() {
        symbols.forEach(symbol -> {
            final CoinCapAssetEntity entity = CoinCapMapper.INSTANCE.toEntity(coinCapClient.getPrice(symbol), symbol);
            coinCapAssetRepository.save(entity);
        });
    }
}
