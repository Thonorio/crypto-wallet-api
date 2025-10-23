package com.swiss_post.crypto_wallet.model;

import java.math.BigDecimal;

public record WalletSimulationResult(
        BigDecimal total,
        String bestAsset,
        BigDecimal bestPerformance,
        String worstAsset,
        BigDecimal worstPerformance
) {}