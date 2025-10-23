package com.swiss_post.crypto_wallet.service;

import com.swiss_post.crypto_wallet.model.CreateWalletRequest;
import com.swiss_post.crypto_wallet.model.SimulationAsset;
import com.swiss_post.crypto_wallet.model.WalletSimulationResult;
import com.swiss_post.crypto_wallet.model.Asset;
import com.swiss_post.crypto_wallet.entity.AssetEntity;
import com.swiss_post.crypto_wallet.entity.CoinCapAssetEntity;
import com.swiss_post.crypto_wallet.entity.UserEntity;
import com.swiss_post.crypto_wallet.entity.WalletEntity;
import com.swiss_post.crypto_wallet.infrastructure.exceptions.TokenNotFoundException;
import com.swiss_post.crypto_wallet.model.Wallet;
import com.swiss_post.crypto_wallet.repository.AssetRepository;
import com.swiss_post.crypto_wallet.repository.CoinCapAssetRepository;
import com.swiss_post.crypto_wallet.repository.UserRepository;
import com.swiss_post.crypto_wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    private final UserRepository userRepository;
    private final CoinCapAssetRepository coinCapAssetRepository;

    public WalletService(WalletRepository walletRepository,
                         UserRepository userRepository,
                         AssetRepository assetRepository,
                         CoinCapAssetRepository coinCapAssetRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.coinCapAssetRepository = coinCapAssetRepository;
    }

    public Wallet getWallet(final String id){

        final WalletEntity wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        final List<Asset> assetList = wallet.getAssets().stream().map(assetEntity ->
                new Asset(assetEntity.getSymbol(), assetEntity.getQuantity(), assetEntity.getPrice(), assetEntity.getValue())).toList();


        return new Wallet(wallet.getId(), wallet.getTotalValue(), assetList);
    }

    @Transactional
    public Wallet createNewWallet(final CreateWalletRequest request) {
        final String walletId = UUID.randomUUID().toString();

        final UserEntity user = userRepository.findByEmail(request.email())
                .orElseGet(() -> userRepository.save(UserEntity.builder().email(request.email()).build()));

        final WalletEntity walletEntity = WalletEntity.builder()
                .id(walletId)
                .user(user)
                .build();

        walletRepository.save(walletEntity);

        return new Wallet(walletId, BigDecimal.ZERO, List.of());
    }

    @Transactional
    public Wallet addAssetToWallet(final String walletId, final String symbol, final BigDecimal quantity, final BigDecimal price) {

        coinCapAssetRepository.findBySymbol(symbol)
                .orElseThrow(() -> new TokenNotFoundException(symbol));

        final WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        final AssetEntity assetEntity = AssetEntity.builder()
                .symbol(symbol)
                .quantity(quantity)
                .price(price)
                .build();

        wallet.addAsset(assetEntity);
        walletRepository.save(wallet);

        final List<Asset> assetList = wallet.getAssets().stream()
                .map(a -> new Asset(a.getSymbol(), a.getQuantity(), a.getPrice(), a.getValue()))
                .toList();

        return new Wallet(wallet.getId(), wallet.getTotalValue(), assetList);
    }

    public WalletSimulationResult simulateWallet(final List<SimulationAsset> assets, final LocalDate date) {

        final ZonedDateTime startOfDay = date.atStartOfDay(ZoneOffset.UTC);
        final ZonedDateTime endOfDay = date.atStartOfDay(ZoneOffset.UTC).plusDays(1);

        long start = startOfDay.toInstant().toEpochMilli();
        long end = endOfDay.toInstant().toEpochMilli();

        final Map<String, Optional<CoinCapAssetEntity>> assetMap = assets.stream()
                .collect(Collectors.toMap(
                        SimulationAsset::symbol,
                        asset -> coinCapAssetRepository.findTopBySymbolAndTimestampBetweenOrderByTimestampDesc(asset.symbol(), start, end)
                ));

        BigDecimal total = BigDecimal.ZERO;
        String bestAsset = null;
        String worstAsset = null;
        BigDecimal bestPerf = BigDecimal.valueOf(-9999);
        BigDecimal worstPerf = BigDecimal.valueOf(9999);

        for (SimulationAsset asset : assets) {
            BigDecimal originalPrice = asset.value();
            BigDecimal newPrice = assetMap.get(asset.symbol())
                    .orElseThrow(() -> new TokenNotFoundException(asset.symbol()))
                    .getPriceUSD();


            BigDecimal performance = newPrice.subtract(originalPrice)
                    .divide(originalPrice, 8, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            total = total.add(newPrice.multiply(asset.quantity()));

            if (performance.compareTo(bestPerf) > 0) {
                bestPerf = performance;
                bestAsset = asset.symbol();
            }
            if (performance.compareTo(worstPerf) < 0) {
                worstPerf = performance;
                worstAsset = asset.symbol();
            }
        }

        return new WalletSimulationResult(
                total.setScale(2, RoundingMode.HALF_UP),
                bestAsset,
                bestPerf.setScale(2, RoundingMode.HALF_UP),
                worstAsset,
                worstPerf.setScale(2, RoundingMode.HALF_UP)
        );
    }

}