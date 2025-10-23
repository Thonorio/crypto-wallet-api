package com.swiss_post.crypto_wallet.infrastructure.mappers;

import com.swiss_post.crypto_wallet.infrastructure.connectors.dto.CoinCapResponse;
import com.swiss_post.crypto_wallet.entity.CoinCapAssetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CoinCapMapper {

    CoinCapMapper INSTANCE = Mappers.getMapper(CoinCapMapper.class);

    default CoinCapAssetEntity toEntity(CoinCapResponse asset, String symbol) {
        if (asset == null) {
            return null;
        }
        return CoinCapAssetEntity.builder()
                .symbol(symbol)
                .timestamp(asset.getTimestamp())
                .data(asset.getData())
                .build();
    }
}