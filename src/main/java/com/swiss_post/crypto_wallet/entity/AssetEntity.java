package com.swiss_post.crypto_wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assets")
public class AssetEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private WalletEntity wallet;

    private String symbol;

    private BigDecimal quantity;

    private BigDecimal price;

    @Transient
    public BigDecimal getValue() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(quantity);
    }
}
