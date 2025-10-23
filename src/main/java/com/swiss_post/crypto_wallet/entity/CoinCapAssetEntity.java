package com.swiss_post.crypto_wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coin_cap_assets")
public class CoinCapAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long timestamp;

    private String symbol;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "asset_data",
        joinColumns = @JoinColumn(name = "asset_id")
    )
    @Column(name = "data")
    private List<String> data;

    public BigDecimal getPriceUSD(){
        if (data == null || data.isEmpty() || data.get(0) == null) {
            throw new IllegalStateException("Missing USD price in asset data for id=" + id);
        }
        try {
            return new BigDecimal(data.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid USD price format in asset data for id=" + id, e);
        }
    }
}