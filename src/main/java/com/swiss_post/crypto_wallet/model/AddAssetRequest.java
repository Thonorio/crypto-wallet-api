package com.swiss_post.crypto_wallet.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddAssetRequest(@NotNull String symbol, @NotNull BigDecimal quantity, @NotNull BigDecimal price) {}