package com.swiss_post.crypto_wallet.repository;

import com.swiss_post.crypto_wallet.entity.CoinCapAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinCapAssetRepository extends JpaRepository<CoinCapAssetEntity, String> {

    Optional<CoinCapAssetEntity> findBySymbol(String symbol);

    Optional<CoinCapAssetEntity> findTopBySymbolAndTimestampBetweenOrderByTimestampDesc(String symbol, long start, long end);
}
