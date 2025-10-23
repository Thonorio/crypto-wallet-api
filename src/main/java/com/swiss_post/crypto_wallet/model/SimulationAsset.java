package com.swiss_post.crypto_wallet.model;

import java.math.BigDecimal;

public record SimulationAsset(String symbol, BigDecimal quantity, BigDecimal value) {}