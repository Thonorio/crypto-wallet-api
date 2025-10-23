package com.swiss_post.crypto_wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallets")
public class WalletEntity {

    @Id
    private String id;

    @OneToOne
    private UserEntity user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetEntity> assets = new ArrayList<>();

    public BigDecimal getTotalValue() {
        return assets.stream().map(AssetEntity::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addAsset(AssetEntity asset) {
        asset.setWallet(this);
        assets.add(asset);
    }
}

